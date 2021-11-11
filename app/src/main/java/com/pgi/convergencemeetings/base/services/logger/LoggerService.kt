package com.pgi.convergencemeetings.base.services.logger

import androidx.annotation.VisibleForTesting
import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.InternetConnection
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.NetworkRequestManager
import com.pgi.network.NetworkResponseHandler
import retrofit2.Call
import retrofit2.Response
import java.util.*
import java.util.concurrent.CountDownLatch

class LoggerService : NetworkResponseHandler {
  private var mLogTimer: Timer? = null
  @VisibleForTesting
  var mNetworkRequestManager: NetworkRequestManager
  private val mSharedPreferencesManager: SharedPreferencesManager
  private val mlogger = CoreApplication.mLogger
  private val tag = LoggerService::class.java.simpleName
  private val USE_SESSION = "usesession"
  private val NEW_SESSION = "newsession"
  private val SET_COOKIE = "Set-Cookie"
  private val CODE_202 = 202
  private var mNumberofUploadedMessages = 0
  private val STARTUP_DELAY: Long = 90000
  private var mSessionEndTime = 0L
  private val ONE_HOUR_IN_MILLIS: Long = 3600000
  private var latch = CountDownLatch(0)
  private var logSessionType = NEW_SESSION
  private var cookies: String? = null
  private var retryCount = 0
  public var isTesting = false;

  /**
   * Instantiates a new Logger service.
   */
  init {
    mNetworkRequestManager = NetworkRequestManager(this)
    mSharedPreferencesManager = SharedPreferencesManager.getInstance()
    resetLogSession()
    startLogTask()
  }

  /**
   * Reset log session
   */
  private fun resetLogSession() {
    mSharedPreferencesManager.prefLogSessionCookie = null
    mSessionEndTime = System.currentTimeMillis() + ONE_HOUR_IN_MILLIS
  }

  /**
   * Start log task.
   */
  private fun startLogTask() {
    var timerInterval: Long = 63000
    if (InternetConnection.isConnectedWifi(CoreApplication.appContext)) {
      timerInterval = 31000
    }
    val logTask = LoggerTimerTask()
    mLogTimer = Timer()
    mLogTimer?.scheduleAtFixedRate(logTask, STARTUP_DELAY, timerInterval)
  }

  /**
   * Stop log task.
   */
  fun stopLogTask() {
    mLogTimer?.cancel()
    mLogTimer?.purge()
  }

  fun flush() {
    sendLog()
  }

  /**
   * sendLog send any queued log records
   *
   */
  private fun sendLog() {
    // if no network connection then nothing to do
    if ((!PGiIdentityAuthService.getInstance(CoreApplication.appContext).authState()
            .getCurrent().isAuthorized || !InternetConnection.isConnected(CoreApplication
            .appContext)) && !isTesting) {
      return
    }
    latch.await()
    logSessionType = NEW_SESSION
    cookies = mSharedPreferencesManager.prefLogSessionCookie
    if (cookies != null) {
      logSessionType = USE_SESSION
    }
    val realm = mlogger.getRealmInstance()
    if (realm != null) {
      realm.refresh()
      val (logs, count) = mlogger.getlogsAndCount(realm, 20)
      mNumberofUploadedMessages = count
      if (mNumberofUploadedMessages == 0) {
        return
      }
      realm.close()
      latch = CountDownLatch(1)
      try {
        mNetworkRequestManager.sendLogs(logSessionType, cookies, logs)
      } catch (e: Exception) {
        mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.ELK_LOG, e.message.toString(), e)
      }
    }
  }

  override fun onResponse(call: Call<String>, response: Response<String>) {
    val responseCode = response.code()
    if (responseCode == CODE_202) {
      val headers = response.headers()
      if (headers != null && headers.size() > 0) {
        val cookieHeader = headers.get(SET_COOKIE)
        if (cookieHeader != null && cookieHeader.length > 0) {
          val cookieMsg = "SET_COOKIE=$cookieHeader"
          mSharedPreferencesManager.prefLogSessionCookie = cookieHeader
        }
      }
      retryCount = 0
      deleteUploadedLogs()
    } else {
      if (responseCode == 413) {
        val realm = mlogger.getRealmInstance()
        if (realm != null) {
          val max = mNumberofUploadedMessages / 2
          mlogger.error(tag, LogEvent.ERROR, LogEventValue.ELK_LOG, "LoggerService onResponse()" +
              " - rc=413, reducing count to $max and retrying")
          val (logs, count) = mlogger.getlogsAndCount(realm, max)
          mNumberofUploadedMessages = count
          try {
            mNetworkRequestManager.sendLogs(logSessionType, cookies, logs)
          } catch (e: Exception) {
            mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.ELK_LOG, e.message.toString(), e)
          }
        }
      } else if (responseCode >= 500  && retryCount < 3) {
        val realm = mlogger.getRealmInstance()
        if (realm != null) {
          mlogger.error(tag, LogEvent.ERROR, LogEventValue.ELK_LOG, "LoggerService onResponse()" +
              " - rc=$responseCode, retrying")
          val (logs, count) = mlogger.getlogsAndCount(realm, mNumberofUploadedMessages)
          mNumberofUploadedMessages = count
          retryCount++
          try {
            mNetworkRequestManager.sendLogs(logSessionType, cookies, logs)
          } catch (e: Exception) {
            mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.ELK_LOG, e.message.toString(), e)
          }
        }
      } else if (responseCode in 400..499) {
        mlogger.error(tag, LogEvent.ERROR, LogEventValue.ELK_LOG, "LoggerService onResponse()" +
            " - response code = $responseCode. Aborting logging attempt.")
      } else {
        mlogger.error(tag, LogEvent.ERROR, LogEventValue.ELK_LOG, "LoggerService onResponse()" +
            " - unexpected response code = $responseCode, deleting log records")
        deleteUploadedLogs()
      }
    }
  }

  override fun onFailure(call: Call<String>, t: Throwable) {
    mlogger.error(tag, LogEvent.EXCEPTION, LogEventValue.ELK_LOG, "LoggerService onFailure()" +
        " - failed uploading logs", t)
    latch.countDown()
  }


  //deletes uploaded logs from DB
  internal fun deleteUploadedLogs() {
    val realm = mlogger.getRealmInstance()
    if (realm != null) {
      mlogger.deleteLogs(mNumberofUploadedMessages, realm)
      mNumberofUploadedMessages = 0
      realm.close()
      latch.countDown()
    }
  }

  /**
   * The type Logger timer task.
   */
  internal inner class LoggerTimerTask : TimerTask() {
    override fun run() {
      sendLog()
      if (System.currentTimeMillis() > mSessionEndTime) {
        resetLogSession()
      }
    }
  }
}
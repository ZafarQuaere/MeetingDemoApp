package com.pgi.convergencemeetings.base

import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.di.baseModule
import com.pgi.convergencemeetings.base.services.logger.LoggerService
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import okio.Buffer
import okio.buffer
import okio.source
import org.json.JSONObject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.io.IOException
import java.nio.charset.Charset

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class ConvergenceApplication : CoreApplication() {

  override fun onCreate() {
    super.onCreate()
    startKoin {
      androidLogger()
      androidContext(this@ConvergenceApplication)
      modules(baseModule)
    }
    parseSupportEmail()
  }

  override fun initLogger() {
    super.initLogger()
    mLoggerService = LoggerService()
  }

  fun parseSupportEmail(){
    val configSource = resources.openRawResource(R.raw.env).source().buffer()
    val configData = Buffer()
    try {
     configSource.readAll(configData)
      val mConfigJson = JSONObject(configData.readString(Charset.forName("UTF-8")))
      SharedPreferencesManager.getInstance().supportEmail = mConfigJson.getString("support_email")
      }
    catch (ex : IOException) {
      mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.ENVIRONMENT_CONFIG_PARSE, "ConvergenceApplication parseSupportEmail()", ex, null, true, false)

    }
  }

  companion object {
    val TAG = ConvergenceApplication::class.java.simpleName
    lateinit var mLoggerService: LoggerService
  }
}

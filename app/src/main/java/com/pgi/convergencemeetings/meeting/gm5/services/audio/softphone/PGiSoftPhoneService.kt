package com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pgi.convergence.annotations.OpenForTest
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.constants.AppConstants.ANDROID_CHANNEL_ID
import com.pgi.convergence.utils.ForegroundServiceLauncher
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.NotificationId
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.models.Cave


@OpenForTest
class PGiSoftPhoneService : Service() {

  private var mNotificationManager: NotificationManager? = null
	private var wearableExtender: NotificationCompat.Builder? = null
  private var mLogger = CoreApplication.mLogger
  var mfurl: String? = null
  private lateinit var className: Class<*>
  private val TAG: String = PGiSoftPhoneService::class.java.simpleName
  private var speakerActive: Boolean = false
  var mute: Boolean = false
  private var lastNetworkChange = System.currentTimeMillis()

  companion object {
    var mPGiSoftPhone: PGiSoftPhone? = null
    private val LAUNCHER = ForegroundServiceLauncher(PGiSoftPhoneService::class.java)

    @JvmStatic
    fun start(context: Context,  intent: Intent) = LAUNCHER.startService(context, intent)

//    Not needed for now but if we need to stop service from different context we need to use this
//    @JvmStatic
//    fun stop(context: Context) = LAUNCHER.stopService(context)
  }

  override fun onCreate() {
    super.onCreate()
    try {
      mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    } catch (ex: Exception) {
      mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.VOIP_SERVICE,
          "PGiSoftPhoneService onCreate() - Failed getting Notification Service", ex)
    }
    startOnGoingNotification()
    mLogger.info(TAG, LogEvent.SERVICE_NOTIFICATION, LogEventValue.ONGOING_MEETING_NOTIFICATION,
        "PGiSoftPhoneService - Started OnGoing Meeting Notification as a foregroundService")
    LAUNCHER.onServiceCreated(this)
  }

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent == null) {
      return START_NOT_STICKY
    }
    this.className = Class.forName(intent.getStringExtra("targetClass"))
    Log.e(TAG, "PGiSoftPhoneService action= ${intent.action}")
    when (intent.action) {
      PGiSoftPhoneConstants.VOIP_INIT.name -> {
        startOnGoingNotification()
        if(mPGiSoftPhone == null) {
          mPGiSoftPhone = PGiSoftPhoneImpl(this, mLogger)
        } else {
          mPGiSoftPhone?.isSoftPhoneAvailable()
        }
      }
      PGiSoftPhoneConstants.VOIP_DIALOUT.name -> {
        val sipFromAddress: String = intent.getStringExtra("sipFromAddress") ?: ""
        val formattedSipUrl: String = intent.getStringExtra("formattedSipUrl") ?: ""
        val dolby: String = intent.getStringExtra("enableDolby") ?: ""
        var proxyServer : String = ""
        if (intent.hasExtra("caveInfo")) {
          val caveInfo = intent.getSerializableExtra("caveInfo") as Cave
          caveInfo.sipTls?.let {
            proxyServer = it
          }
        }
        Log.e(TAG, "dolby=$dolby")
        var enableDolby = true
        if (dolby == "false") {
          enableDolby = false
        }
        mfurl = intent.getStringExtra("furl") ?: ""
        if (mPGiSoftPhone == null) {
          mPGiSoftPhone = PGiSoftPhoneImpl(this, mLogger)
        }
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - Making a dialout")
        mPGiSoftPhone?.dialOut(sipFromAddress, formattedSipUrl, enableDolby, proxyServer)
        startVoipNotification()
        mLogger.info(TAG, LogEvent.SERVICE_NOTIFICATION, LogEventValue.VOIP_NOTIFICATION,
            "PGiSoftPhoneService - Started Voip Notification as a foregroundService")
      }
      PGiSoftPhoneConstants.VOIP_ACTIVATE_SPEAKER.name -> {
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - Activating Speaker")
        mPGiSoftPhone?.activateSpeaker()
        speakerActive = true
        startVoipNotification()
      }
      PGiSoftPhoneConstants.VOIP_STOP.name -> {
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - Hanging up SoftPhone")
        mPGiSoftPhone?.hangUp()
        sendVoIPCallQualityLog(mPGiSoftPhone?.getCallQuality())
      }
      PGiSoftPhoneConstants.VOIP_PAUSE.name -> {
        mPGiSoftPhone?.pauseAudio()
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - Pausing SoftPhone")
      }
      PGiSoftPhoneConstants.VOIP_RESUME.name -> {
        mPGiSoftPhone?.resumeAudio()
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - Resuming SoftPhone")
      }
      PGiSoftPhoneConstants.VOIP_SET_AUDIO_ROUTE.name -> {
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - Set Default Audio Route (turn speaker off)")
        mPGiSoftPhone?.setDefaultAudioRoute()
        speakerActive = false
        startVoipNotification()
      }
      PGiSoftPhoneConstants.VOIP_MIC_ACTIVE.name -> {
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - VOIP_MIC_ACTIVE")
        mute = false
        startVoipNotification()
    }
      PGiSoftPhoneConstants.VOIP_MIC_MUTED.name -> {
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - VOIP_MIC_MUTED")
        mute = true
        startVoipNotification()
      }
      PGiSoftPhoneConstants.VOIP_RAISE_VOLUME.name -> {
        mPGiSoftPhone?.raiseVolume()
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - Raising Volume")
      }
      PGiSoftPhoneConstants.VOIP_LOWER_VOLUME.name -> {
        mPGiSoftPhone?.lowerVolume()
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - Lowering Volume")
      }
      PGiSoftPhoneConstants.VOIP_RELEASE_FOCUS.name -> {
        mPGiSoftPhone?.releaseFocus()
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - Releasing Focus")
      }
      PGiSoftPhoneConstants.VOIP_REQUEST_FOCUS.name -> {
        mPGiSoftPhone?.requestFocus()
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - Requesting Focus")
      }
      PGiSoftPhoneConstants.VOIP_NETWORK_CHANGE.name ->{
        Log.e(TAG, "PGiSoftPhoneService - Network change")
        val diff = System.currentTimeMillis() - lastNetworkChange
        if (diff > 5000) {
          lastNetworkChange = System.currentTimeMillis()
          mPGiSoftPhone?.networkChange()
          mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
              "PGiSoftPhoneService - Network change")
        } else {
          mLogger.error(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE, "ignoring network change")
        }
      }
      else -> {
        mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
            "PGiSoftPhoneService - Destroying SoftPhone on action: ${intent.action}")
        Log.e(TAG, "PGiSoftPhoneService - Destroying SoftPhone on action: ${intent.action}")
        this.destroySoftPhone()
        startOnGoingNotification()
        removeNotification()
        stopForeground(true)
      }
    }
    return START_STICKY
  }

  private fun sendVoIPCallQualityLog(callQuality: String?) {
    if(!callQuality.isNullOrEmpty()) {
      mLogger.mixPanelVoIPCallQuality.callQuality = callQuality
      mLogger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_VOIP_CALL_QUALITY, "Metrics: " + LogEventValue.MIXPANEL_VOIP_CALL_QUALITY.value,
              null, null, false, true)
      mPGiSoftPhone?.resetRAGCount()
    }
  }

  private fun startOnGoingNotification() {
    val notification: Notification? = createOnGoingMeetingNotification()
    if (notification != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForeground(NotificationId.VOIP_AUDIO, notification)
      } else {
        mNotificationManager?.notify(NotificationId.VOIP_AUDIO, notification)
      }
    }
  }

  private fun startVoipNotification() {
    if(mPGiSoftPhone?.isCallConnected() == true) {
      val notification: Notification? = createAndUpdateVoipNotification()
      if (notification != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          startForeground(NotificationId.VOIP_AUDIO, notification)
        } else {
          mNotificationManager?.notify(NotificationId.VOIP_AUDIO, notification)
        }
      } else {
        startOnGoingNotification()
      }
    }
  }

  private fun removeNotification() {
    mNotificationManager?.cancel(NotificationId.VOIP_AUDIO)
    mLogger.info(TAG, LogEvent.SERVICE_SOFTPHONE, LogEventValue.VOIP_SERVICE,
        "PGiSoftPhoneService - Removing Notification")
    stopSelf()
  }

  private fun destroySoftPhone() {
    Log.e(TAG, "destroySoftPhone")
    mPGiSoftPhone?.hangUp()
    mPGiSoftPhone?.destroy()
    mPGiSoftPhone = null
  }

  fun createAndUpdateVoipNotification(): Notification? {
    val notificationIntent = Intent(this, className)
    notificationIntent.putExtra(AppConstants.KEY_FURL, mfurl)
    notificationIntent.action = AppConstants.OPEN_ACTIVITY
    notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

    val stopAudioIntent = Intent(this, className)
    stopAudioIntent.putExtra(AppConstants.KEY_FURL, mfurl)
    stopAudioIntent.action = AppConstants.HANGUP_SOFTPHONE

    val muteAudioIntent = Intent(this, className)
    muteAudioIntent.putExtra(AppConstants.KEY_FURL, mfurl)
    muteAudioIntent.action = AppConstants.MIC_TOGGLE

    val speakerAudioIntent = Intent(this, className)

    speakerAudioIntent.putExtra(AppConstants.KEY_FURL, mfurl)
    speakerAudioIntent.action = AppConstants.SPEAKER_TOGGLE


    val pendingIntent = PendingIntent.getActivity(
        this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val stopPendingIntent = PendingIntent.getActivity(
        this, 1, stopAudioIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val mutePendingIntent = PendingIntent.getActivity(
        this, 2, muteAudioIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val speakerPendingIntent = PendingIntent.getActivity(
        this, 3, speakerAudioIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    val mNotificationCompat: NotificationCompat.Builder = NotificationCompat.Builder(this, ANDROID_CHANNEL_ID)

		mNotificationCompat.setContentTitle(getString(R.string.notification_title))
    mNotificationCompat.setContentText(getString(R.string.notification_content))
    mNotificationCompat.setSubText("Connected using VOIP")

    mNotificationCompat.setSmallIcon(R.drawable.notification_icon_white)
    mNotificationCompat.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

    mNotificationCompat.priority = NotificationCompat.PRIORITY_HIGH

    mNotificationCompat.setContentIntent(pendingIntent)
    mNotificationCompat.setDeleteIntent(stopPendingIntent)
    mNotificationCompat.color = resources.getColor(R.color.brand_color_300)

    if (mute) {
      mNotificationCompat.addAction(android.R.drawable.stat_notify_call_mute, getString(R.string.menu_unmute), mutePendingIntent)
    } else {
      mNotificationCompat.addAction(R.drawable.pstnmeeting_unmute_pressed, getString(R.string.menu_mute), mutePendingIntent)
    }
    if (speakerActive) {
      mNotificationCompat.addAction(android.R.drawable.stat_sys_speakerphone, getString(R.string.turn_off_speaker), speakerPendingIntent)
    } else {
      mNotificationCompat.addAction(R.drawable.audiomeeting_speaker_active_pressed, getString(R.string.turn_on_speaker), speakerPendingIntent)
    }

    mNotificationCompat.setOngoing(true)
		wearableExtender = NotificationCompat.WearableExtender().extend(mNotificationCompat)
		return wearableExtender?.build()
  }

  fun createOnGoingMeetingNotification(): Notification? {
    val mNotificationCompat: NotificationCompat.Builder = NotificationCompat.Builder(this, ANDROID_CHANNEL_ID)

    mNotificationCompat.setContentTitle(getString(R.string.notification_title))
    mNotificationCompat.setContentText(getString(R.string.notification_content))

    mNotificationCompat.setSmallIcon(R.drawable.notification_icon_white)
    mNotificationCompat.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

    mNotificationCompat.priority = NotificationCompat.PRIORITY_HIGH
    if (::className.isInitialized) {
      val notificationIntent = Intent(this, className)
      notificationIntent.putExtra(AppConstants.KEY_FURL, mfurl)
      notificationIntent.action = AppConstants.OPEN_ACTIVITY
      notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

      val pendingIntent = PendingIntent.getActivity(
        this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
       mNotificationCompat.setContentIntent(pendingIntent)
    }

    mNotificationCompat.color = resources.getColor(R.color.brand_color_300)

    mNotificationCompat.setOngoing(true)
		wearableExtender = NotificationCompat.WearableExtender().extend(mNotificationCompat)
    return wearableExtender?.build()
  }
}
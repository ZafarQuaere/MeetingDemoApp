package com.pgi.convergence.application

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.StrictMode
import androidx.annotation.VisibleForTesting
import com.convergence.core.BuildConfig
import com.facebook.stetho.Stetho
import com.github.anrwatchdog.ANRError
import com.github.anrwatchdog.ANRWatchDog
import com.pgi.convergence.utils.CommonUtils
import com.pgi.logging.Logger
import com.pgi.logging.PGiLogger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue

/**
 * Created by Sudheer Chilumula on 2019-01-11.
 * PGi
 * sudheer.chilumula@pgi.com
 */
open class CoreApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		appContext = this.applicationContext
		mResources = this.resources
		CommonUtils.setContext(appContext)
		CommonUtils.setResources(mResources)
		initLogger()
		setUpDevTools()
	}

	open fun initLogger() {
		if (!isRoboUnitTest) {
			mLogger = PGiLogger(appContext)
			mLogger.initLocalDB()
			mLogger.startMetric(LogEvent.METRIC_COLD_START)
		}
	}

	private fun setUpDevTools() {
		if (!isRoboUnitTest) {
			if (BuildConfig.DEBUG) {
				Stetho.initializeWithDefaults(this)
				StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
						.detectNetwork()
						.permitDiskWrites()
						.permitDiskReads()
						.penaltyLog()
						.penaltyFlashScreen()
						.build())
				StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
						.detectLeakedSqlLiteObjects()
						.detectLeakedClosableObjects()
						.penaltyLog() // .penaltyDeath()
						.build())
			} else {
				ANRWatchDog(10000)
						.setIgnoreDebugger(true)
						.setReportMainThreadOnly()
						.setANRListener { error: ANRError -> anrListener(error) }.start()
				uncaughtExceptionHandlers()
			}
		}
	}

	fun anrListener(error: ANRError) {
		mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.EXCEPTION_ANR, "ERROR_ANR Exception",
				error.fillInStackTrace(),
				null, true, false)
	}

	private fun uncaughtExceptionHandlers() {
		val priorExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
		Thread.setDefaultUncaughtExceptionHandler { thread: Thread?, throwable: Throwable? ->
			mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.EXCEPTION_UNHANDLED, "Unhandled " +
					"exception", throwable, null, true, false)
			priorExceptionHandler.uncaughtException(thread, throwable)
		}
	}

	companion object {
		// We need a better mechanism here. This won't cause issue as we are initializing these values onCreate
		@set:VisibleForTesting
		lateinit var appContext: Context
		lateinit var mResources: Resources
    lateinit var mLogger: Logger
		val TAG = CoreApplication::class.java.simpleName
		val isRoboUnitTest: Boolean
			get() = "robolectric" == Build.FINGERPRINT
	}
}
package com.pgi.convergence.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.convergence.core.R
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class AppUpgrade {
    companion object {
        private const val TAG = "AppUpgrade"
        private const val playStoreUri = "https://play.google.com/store/apps/details?id="
        private const val userAgent = "User-Agent"

        /*
         * functions for handling retrieving PlayStore app version information
         *
         * Note that the downloading of the appstore information is time consuming
         * so it starts a background thread so it won't interfere with the UI
         *
         * It stores the retrieved data in the shared preferences
        */

        fun downloadAppInfoFromPlayStore(activity: Activity, delayMillis: Int) {
            activity?.let {
                val handlerThread = HandlerThread("com.pgi.gmmeet.playstore")
                handlerThread.start()
                val h = Handler(handlerThread.looper)
                h.postDelayed({
                    loadPlayStoreAppVersion(activity)
                    Looper.myLooper()!!.quit()
                }, delayMillis.toLong())  // wait delay milliseconds before downloading
            }
        }

        private fun findStringMatchingPattern(patternString: String, inputString: String): String? {
            try {
                //Create a pattern
                val pattern = Pattern.compile(patternString) ?: return null
                //Match the pattern string in provided string
                val matcher = pattern.matcher(inputString)
                if (matcher != null && matcher.find()) {
                    return matcher.group(1)
                }
            } catch (ex: PatternSyntaxException) {
                CoreApplication.mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.APPUPGRADE_LOAD, "findStringMatchingPattern", ex, null,
                    false, false)
            }
            return null
        }

        private fun loadPlayStoreAppVersion(activity: Activity) {
            val dateFormat = SimpleDateFormat("yyyyMMddhhmmss")
            var ts: String? = null
            try {
                val date = Date()
                ts = dateFormat.format(date)
            } catch (ex: Exception) {
                CoreApplication.mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.APPUPGRADE_LOAD, "getPlayStoreAppVersion.date", ex, null,
                    false, false)
            }
            val packageName = activity.packageName ?: return
            var appUrlString = playStoreUri + packageName
            if (ts != null) {
                appUrlString += "&ts=$ts"
            }
            val urlData = StringBuilder()
            var uc : HttpURLConnection? = null
            try {
                val url = URL(appUrlString)
                uc = url.openConnection() as HttpURLConnection
                val defaultUA = System.getProperty("http.agent")
                uc?.setRequestProperty(userAgent, defaultUA)
                BufferedReader(InputStreamReader(uc?.getInputStream())).use {
                    it.let { reader ->
                        reader.lineSequence().forEach { line ->
                            urlData.append(line)
                        }
                    }
                }
            } catch (ex: Exception) {
                CoreApplication.mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.APPUPGRADE_LOAD, "getPlayStoreAppVersion.download", ex, null,
                    false, false)
            } finally {
                uc?.disconnect()
            }
            val currentVersionPatternSeq = "<div[^>]*?>Current\\sVersion</div><span[^>]*?>(.*?)><div[^>]*?>(.*?)><span[^>]*?>(.*?)</span>"
            val versionString = findStringMatchingPattern(currentVersionPatternSeq, urlData.toString())
            if (versionString != null) {
                // get version from "htlgb">X.X.X</span>
                val appVersionPatternSeq = "htlgb\">([^<]*)</s"
                val appVersion: String? = findStringMatchingPattern(appVersionPatternSeq, versionString)
                if (appVersion != null && !appVersion.isEmpty()) {
                    val sharedPreferencesManager = SharedPreferencesManager.getInstance()
                    sharedPreferencesManager.playStoreVersion = appVersion
                }
            }
        }


        /*
         * functions for do a version comparision between current app and Play Store version information
         *
         * These functions use the Play Store app information loaded from the code above and
         * stored in the shared preferences.
         *
         */

        private var mIsUpdateDialogVisible = false
        private var mSharedPreferencesManager : SharedPreferencesManager? = null

        private fun currentVersionDiff(activity: Activity): Int {
            val storeVersion = getStoreVersion()
            if (storeVersion  == null || !storeVersion.contains(".")) {
                return 0
            }
            val versionParts = storeVersion.split(Pattern.quote(".").toRegex(), 3).toTypedArray()
            val storeMajor = Integer.parseInt(versionParts[0])
            val packageName = activity.packageName
            var version: String? = null
            try {
                val packageInfo = activity.packageManager.getPackageInfo(packageName, 0)
                version = packageInfo.versionName
            } catch (ex: PackageManager.NameNotFoundException) {
                CoreApplication.mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue
                    .APPUPGRADE_VERSION_CHECK, "currentVersionDiff",
                    ex, null, false, false)
            }
            var nVersionDiff = 0
            if (version != null) {
                val myVersionParts = version.split(Pattern.quote(".").toRegex(), 3).toTypedArray()
                val myMajor = Integer.parseInt(myVersionParts[0])
                if (storeMajor > myMajor) {
                    return 99 // so big that it will force an upgrade.
                }
                val storeMinor = Integer.parseInt(versionParts[1])
                val myMinor = Integer.parseInt(myVersionParts[1])
                nVersionDiff = storeMinor - myMinor
            }
            return nVersionDiff
        }

        fun doUpdateCheck(activity: Activity?) {
            activity?.let {
                if (!mIsUpdateDialogVisible && isUpgradeNeeded(activity)) {
                    val alertDialog = AlertDialog.Builder(activity)
                        .setPositiveButton(activity!!.getString(R.string.app_update_alert_button_update)) { dialogInterface, i ->
                            dialogInterface.dismiss()
                            mIsUpdateDialogVisible = false
                            val packageName = activity.packageName
                            if (packageName != null) {
                                val appInStoreUri = playStoreUri + packageName
                                val uri = Uri.parse(appInStoreUri)
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                activity.startActivity(intent)
                            }
                        }
                        .setNegativeButton(activity.getString(R.string.app_update_alert_button_later)) { dialogInterface, i ->
                            dialogInterface.dismiss()
                            mIsUpdateDialogVisible = false
                        }
                        .create()
                    alertDialog.setMessage(activity.getString(R.string.app_update_alert_message))
                    mIsUpdateDialogVisible = true
                    alertDialog.show()
                }
            }
        }

        private fun getStoreVersion(): String? {
            if (mSharedPreferencesManager == null) {
                mSharedPreferencesManager = SharedPreferencesManager.getInstance()
            }
            return mSharedPreferencesManager?.playStoreVersion
        }

        private fun isUpgradeNeeded(activity: Activity): Boolean {
            if (mSharedPreferencesManager == null) {
                mSharedPreferencesManager = SharedPreferencesManager.getInstance()
            }
            var versionMaxDiff : Int? = mSharedPreferencesManager?.droidNumSupportedVersions
            if (versionMaxDiff == null || versionMaxDiff == 0) {
                // if it isn't set, use a default value
                versionMaxDiff = 8
            }
            val versionDiff = currentVersionDiff(activity)
            return versionDiff >= versionMaxDiff
        }

    }
}
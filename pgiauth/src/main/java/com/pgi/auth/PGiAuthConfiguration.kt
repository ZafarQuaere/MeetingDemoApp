package com.pgi.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import android.text.TextUtils
import com.pgi.convergence.utils.KotlinSingletonHolder
import net.openid.appauth.connectivity.ConnectionBuilder
import net.openid.appauth.connectivity.DefaultConnectionBuilder
import okio.Buffer
import okio.buffer
import okio.source
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicReference

class PGiAuthConfiguration private constructor(context: Context) {
    private val TAG = "PGiAuthConfiguration"
    private val PREFS_NAME = "config"
    private val KEY_LAST_HASH = "lastHash"
    private val mContext: Context = context
    private val mPrefs: SharedPreferences
    private val mResources: Resources

    private var mConfigJson: JSONObject? = null
    private var mConfigHash: String? = null
    private var mConfigError: String? = null

    var mClientId: String? = null
    var mScope: String? = null
    var mRedirectUri: Uri? = null
    var mDiscoveryUri: Uri? = null
    var mAuthEndpointUri: Uri? = null
    var mTokenEndpointUri: Uri? = null
    var mRegistrationEndpointUri: Uri? = null
    var mUserInfoEndpointUri: Uri? = null
    var mHttpsRequired: Boolean = false
    var mBrandId: String? = null
    var mLabel: String? = null


    init {
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        mResources = context.resources
        try {
            readConfiguration()
        } catch (ex: InvalidConfigurationException) {
            mConfigError = ex.message
        }

    }

    companion object : KotlinSingletonHolder<PGiAuthConfiguration, Context>({
        val sInstance: AtomicReference<WeakReference<PGiAuthConfiguration>> = AtomicReference(WeakReference<PGiAuthConfiguration>(PGiAuthConfiguration(it)))
        sInstance.get().get()!!
    })

    /**
     * Indicates whether the configuration has changed from the last known valid state.
     */
    fun hasConfigurationChanged(): Boolean {
        val lastHash = getLastKnownConfigHash()
        return mConfigHash != lastHash
    }

    fun getConnectionBuilder(): ConnectionBuilder {
        return DefaultConnectionBuilder.INSTANCE
    }

    private fun getLastKnownConfigHash(): String? {
        return mPrefs.getString(KEY_LAST_HASH, null)
    }

    @Throws(InvalidConfigurationException::class)
    private fun readConfiguration() {
        val configSource = if (BuildConfig.FLAVOR == "dev") {
            mResources.openRawResource(R.raw.auth_config_dev).source().buffer()
        } else if (BuildConfig.FLAVOR == "qac") {
            mResources.openRawResource(R.raw.auth_config_qac).source().buffer()
        } else if (BuildConfig.FLAVOR == "qab") {
            mResources.openRawResource(R.raw.auth_config_qab).source().buffer()
        }
        else if(BuildConfig.FLAVOR == "lumen") {
            mResources.openRawResource(R.raw.auth_config_ctl).source().buffer()
        }
        else if(BuildConfig.FLAVOR == "lumenqab") {
            mResources.openRawResource(R.raw.auth_config_ctlqab).source().buffer()
        }
        else if(BuildConfig.FLAVOR == "lumenqac") {
            mResources.openRawResource(R.raw.auth_config_ctlqac).source().buffer()
        }
        else if(BuildConfig.FLAVOR == "lumendev") {
            mResources.openRawResource(R.raw.auth_config_ctldev).source().buffer()
        }
        else {
            mResources.openRawResource(R.raw.auth_config).source().buffer()
        }

        val configData = Buffer()
        try {
            configSource.readAll(configData)
            mConfigJson = JSONObject(configData.readString(Charset.forName("UTF-8")))
        } catch (ex: IOException) {
            throw InvalidConfigurationException("Failed to read configuration: " + ex.message)
        } catch (ex: JSONException) {
            throw InvalidConfigurationException("Unable to parse configuration: " + ex.message)
        }

        mConfigHash = configData.sha256().base64()
        mClientId = getConfigString("client_id")
        mScope = getRequiredConfigString("authorization_scope")
        mRedirectUri = getRequiredConfigUri("redirect_uri")
        mBrandId = getRequiredConfigString("brand_id")
        mLabel = getConfigString("label")

        if (!isRedirectUriRegistered()) {
            throw InvalidConfigurationException("redirect_uri is not handled by any activity in this app")
        }

        if (getConfigString("discovery_uri") == null) {
            mAuthEndpointUri = getRequiredConfigWebUri("authorization_endpoint_uri")
            mTokenEndpointUri = getRequiredConfigWebUri("token_endpoint_uri")
            mUserInfoEndpointUri = getRequiredConfigWebUri("user_info_endpoint_uri")
            if (mClientId == null) {
                mRegistrationEndpointUri = getRequiredConfigWebUri("registration_endpoint_uri")
            }
        } else {
            mDiscoveryUri = getRequiredConfigWebUri("discovery_uri")
        }
        mHttpsRequired = mConfigJson!!.optBoolean("https_required", true)
    }

    internal fun getConfigString(propName: String): String? {
        var value: String? = mConfigJson!!.optString(propName) ?: return null
        value = value!!.trim { it <= ' ' }
        return if (TextUtils.isEmpty(value)) {
            null
        } else value
    }

    @Throws(InvalidConfigurationException::class)
    private fun getRequiredConfigString(propName: String): String {
        val value = getConfigString(propName) ?: throw InvalidConfigurationException(
            "$propName is required but not specified in the configuration")
        return value
    }

    @Throws(InvalidConfigurationException::class)
    internal fun getRequiredConfigUri(propName: String): Uri {
        val uriStr = getRequiredConfigString(propName)
        val uri: Uri
        try {
            uri = Uri.parse(uriStr)
        } catch (ex: Throwable) {
            throw InvalidConfigurationException("$propName could not be parsed", ex)
        }
        return uri
    }

    @Throws(InvalidConfigurationException::class)
    internal fun getRequiredConfigWebUri(propName: String): Uri {
        val uri = getRequiredConfigUri(propName)
        val scheme = uri.scheme
        if (TextUtils.isEmpty(scheme) || !("http" == scheme || "https" == scheme)) {
            throw InvalidConfigurationException(
                "$propName must have an http or https scheme")
        }

        return uri
    }

    private fun isRedirectUriRegistered(): Boolean {
        // ensure that the redirect URI declared in the configuration is handled by some activity
        // in the app, by querying the package manager speculatively
        val redirectIntent = Intent()
        redirectIntent.setPackage(mContext.packageName)
        redirectIntent.action = Intent.ACTION_VIEW
        redirectIntent.addCategory(Intent.CATEGORY_BROWSABLE)
        redirectIntent.data = mRedirectUri

        return !mContext.packageManager.queryIntentActivities(redirectIntent, 0).isEmpty()
    }

    class InvalidConfigurationException : Exception {
        internal constructor(reason: String) : super(reason) {}

        internal constructor(reason: String, cause: Throwable) : super(reason, cause) {}
    }
}
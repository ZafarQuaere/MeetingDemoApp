package com.pgi.convergencemeetings.leftnav.about.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.utils.ConvergenceWebView
import com.pgi.logging.enums.Interactions
import java.lang.ref.WeakReference

/**
 * Created by amit1829 on 11/15/2017.
 */
class AboutWebViewActivity : BaseActivity() {
  @BindView(R.id.about_webview_content)
	lateinit var mConvergenceWebView: ConvergenceWebView

  @BindView(R.id.about_webview_progress)
	lateinit var mWebviewProgress: ProgressBar

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setInterActionName(Interactions.ABOUT_WEB_VIEW.interaction)
		setContentView(R.layout.about_web_view)
		ButterKnife.bind(this)
		updateViewWithData()
	}

	@SuppressLint("SetJavaScriptEnabled")
	private fun updateViewWithData() {
		val webSettings = mConvergenceWebView.settings
		webSettings.setAppCachePath(applicationContext.cacheDir.absolutePath)
		webSettings.cacheMode = WebSettings.LOAD_DEFAULT
		webSettings.javaScriptEnabled = true
		webSettings.allowFileAccess = true
		webSettings.setAppCacheEnabled(true)
		webSettings.loadWithOverviewMode = true
		webSettings.useWideViewPort = true
		webSettings.allowContentAccess = true
		mConvergenceWebView.webViewClient = webViewClient
		val bundle = this.intent.extras
		if (bundle != null) {
			val lang = CommonUtils.getLocale(this)
			// ISO 639 code for japanese is JA, but PGI's web site uses JP
			val english = "en"
			val japanese = "jp"
			val netherlands = "nl"
			val sectionType = bundle.getInt(AppConstants.KEY_ABOUT_US_WEB_VIEW_TYPE, -1)
			when (sectionType) {
				AppConstants.ABOUT_ITEM_SOFTWARE_CREDIT -> if (lang.equals(english, ignoreCase = true) || lang.equals(japanese, ignoreCase = true) || lang.equals(netherlands, ignoreCase = true)) {
					mConvergenceWebView.loadUrl(getString(R.string.software_credit_url))
				} else {
					val localeUrl = AppConstants.SOFTWARE_CREDIT_URL_LOCALE.replace("#", lang)
					mConvergenceWebView.loadUrl(localeUrl)
				}
				else -> {
				}
			}
		}
	}

	val webViewClient: WebViewClient = object : WebViewClient() {
		override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
			var boolVal = java.lang.Boolean.TRUE
			if (url.startsWith(AppConstants.WEBVIEW_MAIL_TO_LINK)) {
				val mt = MailTo.parse(url)
				openEmailApp(WeakReference(this@AboutWebViewActivity), arrayOf(mt.to), AppConstants.EMPTY_STRING, AppConstants.EMPTY_STRING)
				view.reload()
			} else {
				if (url.startsWith(AppConstants.WEBVIEW_TEL_LINK)) {
					val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
					startActivity(intent)
				} else if (url.startsWith(AppConstants.WEBVIEW_WEBPAGE_LINK_HTTP) || url.startsWith(AppConstants.WEBVIEW_WEBPAGE_LINK_HTTPS)) {
					view.loadUrl(url)
				}
				boolVal = java.lang.Boolean.FALSE
			}
			return boolVal
		}

		override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
			super.onPageStarted(view, url, favicon)
			mConvergenceWebView.visibility = View.GONE
			mWebviewProgress.visibility = View.VISIBLE
		}

		override fun onPageFinished(view: WebView, url: String) {
			super.onPageFinished(view, url)
			mConvergenceWebView.visibility = View.VISIBLE
			mWebviewProgress.visibility = View.GONE
		}
	}

	@OnClick(R.id.about_back_btn)
	fun onBackBtnClick() {
		finish()
	}
}
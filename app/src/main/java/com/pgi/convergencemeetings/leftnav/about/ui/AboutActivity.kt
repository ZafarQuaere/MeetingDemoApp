package com.pgi.convergencemeetings.leftnav.about.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.common.base.Strings
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.constants.AppConstants.BLANK_SPACE
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.logging.enums.Interactions
import java.lang.ref.WeakReference

class AboutActivity : BaseActivity(), AboutItemClickListener {
  @BindView(R.id.rv_about_recycler_view)
	lateinit var rvAboutView: RecyclerView

  @BindView(R.id.tv_app_version)
	lateinit var tvAppVersion: TextView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_about)
		setInterActionName(Interactions.ABOUT.interaction)
		ButterKnife.bind(this)
		initAboutItems(false)
	}

	fun initAboutItems(test: Boolean) {
		if (!Strings.isNullOrEmpty(getString(R.string.software_credit_url)) || test) {
			val navigationItems = getAboutItems(this)
			val drawerAdapter = AboutListAdapter(navigationItems, this)
			rvAboutView.adapter = drawerAdapter
			rvAboutView.layoutManager = LinearLayoutManager(this)
		}
		// TODO:: format this to use resource placeholders
		tvAppVersion.text = resources.getString(R.string.app_version) + BLANK_SPACE + CommonUtils.getAppVersion(WeakReference(this))
	}

	override fun aboutItemClicked(position: Int) {
		when (position) {
			AppConstants.ABOUT_ITEM_SOFTWARE_CREDIT -> loadAboutWebViewActivity()
			else -> {
			}
		}
	}

	private fun loadAboutWebViewActivity() {
		val termsOfServiceIntent = Intent(this@AboutActivity, AboutWebViewActivity::class.java)
		termsOfServiceIntent.putExtra(AppConstants.KEY_ABOUT_US_WEB_VIEW_TYPE, AppConstants.ABOUT_ITEM_SOFTWARE_CREDIT)
		startActivity(termsOfServiceIntent)
	}

	@OnClick(R.id.iv_about_close)
	fun onAboutClose() {
		finish()
	}
}
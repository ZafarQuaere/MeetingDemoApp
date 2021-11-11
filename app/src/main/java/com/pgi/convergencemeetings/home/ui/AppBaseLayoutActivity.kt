package com.pgi.convergencemeetings.home.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.datatransport.cct.internal.LogEvent
import com.google.android.material.navigation.NavigationView
import com.pgi.auth.PGiIdentityAuthService
import com.pgi.convergence.agenda.ui.MeetingsAgendaViewModel
import com.pgi.convergence.agenda.ui.MeetingsFragment
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.common.features.Features
import com.pgi.convergence.common.features.FeaturesManager
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.JoinMeetingEntryPoint
import com.pgi.convergence.home.ui.HomeCardFragment
import com.pgi.convergence.home.ui.HomeCardsViewModel
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.AppUpgrade
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R.*
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.leftnav.about.ui.AboutActivity
import com.pgi.convergencemeetings.base.utils.getFragment
import com.pgi.convergencemeetings.base.utils.setupWithNavController
import com.pgi.convergencemeetings.databinding.DrawerHeaderLayoutBinding
import com.pgi.convergencemeetings.databinding.NavigationActivityTabBinding
import com.pgi.convergencemeetings.leftnav.NavDrawerActivity
import com.pgi.convergencemeetings.leftnav.settings.ui.SettingActivity
import com.pgi.convergencemeetings.search.ui.SearchActivity
import com.pgi.convergencemeetings.tour.ui.OnBoardAuthActivity
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.logging.enums.LogEventValue
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.navigation_activity_tab.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.lang.ref.WeakReference

@UnstableDefault
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class AppBaseLayoutActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, KoinComponent {

	private var binding: NavigationActivityTabBinding? = null
	private var currentNavController: LiveData<NavController>? = null
	private val baseViewModel: AppBaseViewModel by viewModel()
	private val homeCardsViewModel: HomeCardsViewModel by viewModel()
	private val meetingsAgendaViewModel: MeetingsAgendaViewModel by viewModel()
	private val mSharedPreferencesManager = SharedPreferencesManager.getInstance()
	private val featureManager: FeaturesManager by inject()
	private val MSAL_REQUEST_CODE = 1001
	private var disposbale: CompositeDisposable = CompositeDisposable()
	private val TAG = BaseActivity::class.java.simpleName


	@SuppressLint("DefaultLocale")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this, layout.navigation_activity_tab)
		featureManager.checkAndUpdateRemoteConfig()
		featureManager.userId = AppAuthUtils.getInstance().emailId
		updateCompanyId()
		disposbale.add(featureManager.appConfigSubject.subscribe {
			setBottomNavVisibility()
		})
		disposbale.add(PGiIdentityAuthService.getInstance(CoreApplication.appContext).tokenSubject
				.subscribe {
					featureManager.fetchRemoteConfig()
				})
		if (savedInstanceState == null) {
			setupBottomNavigationBar()
			setupNavDrawer()
		}
		homeCardsViewModel.topbarBg.observe(this, Observer {
			if (this.getFragment(HomeCardFragment::class.java)?.isVisible == true) {
				binding?.toolbar?.setBackgroundColor(it)
			}
		})
		homeCardsViewModel.launchUrl.observe(this, Observer {
			when {
				it.toLowerCase() == baseViewModel.getJoinUrl()?.toLowerCase() -> launchRoom(it, JoinMeetingEntryPoint.START_MEETING, true)
				homeCardsViewModel.entryPoint.value.equals(JoinMeetingEntryPoint.HOME_URL.getValue()) -> launchRoom(it, JoinMeetingEntryPoint.HOME_URL, false)
				else -> launchRoom(it, JoinMeetingEntryPoint.HOME_CARD, false)
			}
		})
		meetingsAgendaViewModel.launchUrl.observe(this, Observer {
			if (it.toLowerCase() == baseViewModel.getJoinUrl()?.toLowerCase()) {
				launchRoom(it, JoinMeetingEntryPoint.START_MEETING, true)
			} else {
				launchRoom(it, JoinMeetingEntryPoint.HOME_AGENDA_LIST, false)
			}
		})
		AppUpgrade.doUpdateCheck(this)
		setConnectionValues()

	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
		super.onRestoreInstanceState(savedInstanceState)
		setupBottomNavigationBar()
		setupNavDrawer()
		setUpSharedData()
	}

	private fun setupNavDrawer() {
		val headerView = navigationView.getHeaderView(0)
		val headerBinding = DrawerHeaderLayoutBinding.bind(headerView)
		headerBinding?.let {
			it.profilePic = baseViewModel.getProfilePic()
			it.initials = baseViewModel.getInitials()
			it.fullName = baseViewModel.getFullName()
			val imageView = it.drawerHeaderAvatar
			if (!it.profilePic.isNullOrEmpty()) {
				Picasso.get()
						.load(it.profilePic)
						.fit()
						.into(imageView)
			}
			it.executePendingBindings()
		}
		navigationView.menu[1].isVisible = baseViewModel.isHost()
	}

	private fun setBottomNavVisibility() {
		val bottomView = binding?.bottomNav
		if (featureManager.isFeatureEnabled(Features.HOME.feature)) {
			bottomView?.menu?.getItem(0)?.isVisible = true
		}
		if (featureManager.isFeatureEnabled(Features.AGENDA.feature)) {
			bottomView?.menu?.getItem(1)?.isVisible = true
		}
	}

	private fun setupBottomNavigationBar() {
		val bottomNavigationView = binding?.bottomNav
		val navGraphIds = listOf(navigation.home, navigation.agenda)
		// Setup the bottom navigation view with a list of navigation graphs
		val controller = bottomNavigationView?.setupWithNavController(
				navGraphIds = navGraphIds,
				fragmentManager = supportFragmentManager,
				containerId = id.base_nav_host_container,
				intent = intent)
		setBottomNavVisibility()
		// Whenever the selected controller changes, setup the action bar.
		controller?.observe(this, Observer { navController ->
			val appBarConfiguration = AppBarConfiguration(navController.graph, binding?.drawerLayout)
			setSupportActionBar(binding?.toolbar)
			val toggle = ActionBarDrawerToggle(this, binding?.drawerLayout, binding?.toolbar, string
					.open_drawer, string.close_drawer)
			binding?.drawerLayout?.addDrawerListener(toggle)
			toggle.syncState()
			navigationView.setupWithNavController(navController)
			setupActionBarWithNavController(navController, appBarConfiguration)
			setupActionBarWithNavController(navController, binding?.drawerLayout)
			navController.addOnDestinationChangedListener { nc, nd, _ ->
				if (nd.id == nc.graph.startDestination) {
					binding?.drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
				} else {
					binding?.drawerLayout?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
				}
			}
			NavigationUI.setupWithNavController(navigationView, navController)
			navigationView.setNavigationItemSelectedListener(this)
			if (navController.currentDestination?.id != id.home_tab) {
				binding?.tvTitle?.text = resources.getText(string.home_tab_meetings)
				binding?.btnSearch?.visibility = View.VISIBLE
				binding?.btnSearch?.let {
					DrawableCompat.setTint(
							it.drawable,
							ContextCompat.getColor(this, color.search_icon_black)
																)
				}
				binding?.toolbar?.setBackgroundColor(Color.parseColor("#f5f5f5"))
				binding?.toolbar?.setNavigationIcon(drawable.ic_menu)
			} else {
				binding?.tvTitle?.text = ""
				homeCardsViewModel.topbarBg.value?.let {
					binding?.toolbar?.setBackgroundColor(it)
				}
				binding?.toolbar?.setNavigationIcon(drawable.ic_menu_white)
				binding?.btnSearch?.visibility = View.VISIBLE
				binding?.btnSearch?.let {
					DrawableCompat.setTint(
							it.drawable,
							ContextCompat.getColor(this, color.white)
																)
				}
			}
			binding?.btnSearch?.let {
				it.setOnClickListener {
					val recentMeetingIntent = Intent(this, SearchActivity::class.java)
					startActivity(recentMeetingIntent)
				}
			}
		})
		currentNavController = controller
	}

	override fun onResume() {
		super.onResume()
		logOpenApp()
		setUpSharedData()
		getFragment(HomeCardFragment::class.java)?.loginName = baseViewModel.getUserFirstName()
		if (this.getFragment(HomeCardFragment::class.java)?.isVisible == true) {
			homeCardsViewModel.topbarBg.value?.let {
				binding?.toolbar?.setBackgroundColor(it)
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		disposbale.clear()
	}

	private fun setUpSharedData() {
		baseViewModel.getUserFirstName()?.let {
			mSharedPreferencesManager.firstName = it
		}
		baseViewModel.getUserLastName()?.let {
			mSharedPreferencesManager.lastName = it
		}
		baseViewModel.getJoinUrl()?.let {
			mSharedPreferencesManager.joinUrl = it
		}
		baseViewModel.getProfilePic()?.let {
			mSharedPreferencesManager.profileImage = it
		}
		baseViewModel.getConfId().let {
			mSharedPreferencesManager.confId = it
		}
		mSharedPreferencesManager.guestUser(AppAuthUtils.getInstance().isUserTypeGuest)
	}

	override fun onSupportNavigateUp(): Boolean {
		return currentNavController?.value?.navigateUp() ?: false
	}

	/**
	 * Overriding popBackStack is necessary in this case if the app is started from the deep link.
	 */
	override fun onBackPressed() {
		if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
			binding?.drawerLayout?.closeDrawer(GravityCompat.START)
		} else if (currentNavController?.value?.popBackStack() != true) {
			super.onBackPressed()
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == MSAL_REQUEST_CODE) {
			if (getFragment(MeetingsFragment::class.java)?.viewModel?.invokedRedirect != null) {
				if (getFragment(MeetingsFragment::class.java)?.viewModel?.invokedRedirect == true) {
					getFragment(MeetingsFragment::class.java)?.handleO365Redirect(requestCode, resultCode, data)
				}
			} else {
				getFragment(HomeCardFragment::class.java)?.handleO365Redirect(requestCode, resultCode, data)
			}
		}
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		drawer_layout.closeDrawers()
		val appSubject = resources.getString(string.help_app_subject)
		val appVersion = resources.getString(string.app_version)
		val opSystemVersion = resources.getString(string.os_version)
		val deviceModels = resources.getString(string.device_model)
		when (item.itemId) {
			id.drawer_profile -> {
				val profileIntent = Intent(this, NavDrawerActivity::class.java)
				this.startActivity(profileIntent)
			}

			id.drawer_settings -> {
				val settingIntent = Intent(this, SettingActivity::class.java)
				this.startActivity(settingIntent)
			}

			id.drawer_about -> {
				val aboutIntent = Intent(this, AboutActivity::class.java)
				this.startActivity(aboutIntent)
			}

			id.drawer_help -> {
				val buildVersion = CommonUtils.getAppVersion(WeakReference(this))
				val osVersion = Build.VERSION.RELEASE
				val deviceModel = CommonUtils.getDeviceModelName()
				val customerCare = if (CommonUtils.isUsersLocaleJapan()) {
					listOf(AppConstants.HELP_APP_TO_EMAIL_JA).toTypedArray()
				} else {
					listOf(mSharedPreferencesManager.supportEmail).toTypedArray()
				}
				openEmailApp(
						WeakReference(this),
						customerCare,
						appSubject,
						AppConstants.NEW_LINE_CHARACTER + appVersion + AppConstants.COLON + AppConstants.BLANK_SPACE + buildVersion + AppConstants.NEW_LINE_CHARACTER + opSystemVersion + AppConstants.COLON + AppConstants.BLANK_SPACE + osVersion + AppConstants.NEW_LINE_CHARACTER + deviceModels + AppConstants.COLON + AppConstants.BLANK_SPACE + deviceModel
										)
			}
			id.drawer_signout -> {
				runOnUiThread {
					Toast.makeText(this, string.signing_out, Toast.LENGTH_SHORT).show()
				}
				baseViewModel.signOutUser()
						.subscribeOn(AndroidSchedulers.mainThread())
						.subscribe({
							baseViewModel.revokeUsersToken()
									.subscribeOn(AndroidSchedulers.mainThread())
									.subscribe({
										clearAppData(this)
										val loginIntent = Intent(this, OnBoardAuthActivity::class.java)
										this.startActivity(loginIntent)
										this.finish()
									}, {
										runOnUiThread {
											Toast.makeText(this, string.error_on_signout, Toast.LENGTH_SHORT).show()
										}
									})
						}, {
							runOnUiThread {
								Toast.makeText(this, string.error_on_signout, Toast.LENGTH_SHORT).show()
							}
						})
			}
		}
		return true
	}

	private fun updateCompanyId() {
		ClientInfoDaoUtils.getInstance().email?.let {
			featureManager.companyEmail = it.split("@")[1].trim()
		} ?: run {
			AppAuthUtils.getInstance().emailId?.let {
				featureManager.companyEmail = it.split("@")[1].trim()
			}
		}
		mlogger.userModel.companyId = ClientInfoDaoUtils.getInstance().companyId.toString()
	}
}
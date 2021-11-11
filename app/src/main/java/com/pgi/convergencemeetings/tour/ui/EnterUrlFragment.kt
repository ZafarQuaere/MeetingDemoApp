package com.pgi.convergencemeetings.tour.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.ElkTransactionIDUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.models.enterUrlModel.Detail
import com.pgi.convergencemeetings.models.enterUrlModel.Result
import com.pgi.convergencemeetings.utils.ClientInfoResultCache
import com.pgi.convergencemeetings.utils.JoinMeetingsListener
import com.pgi.logging.Logger
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.pgi.network.models.ImeetingRoomInfo
import com.pgi.network.models.SearchResult
import com.pgi.network.repository.GMElasticSearchRepository.Companion.INSTANCE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault
import java.lang.ref.WeakReference

/**
 * This fragment is showing one input box where user can provide Meeting URL to join meeting.
 * If refresh token expired then refreshing the token from netwok.
 * Special handing for first time guest user logedin.
 */
@UnstableDefault
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class EnterUrlFragment : Fragment(), EnterUrlContractor.View, JoinMeetingsListener {
	private val mLogger: Logger = CoreApplication.mLogger

  @BindView(R.id.tv_enter_url)
	lateinit var edEnterUrlText: EditText

  @BindView(R.id.invalid_url_text)
	lateinit var tvInvalidUrl: TextView

  @BindView(R.id.btn_enter_url_join_meeting)
	lateinit var btnEnterUrl: Button

  @BindView(R.id.menu_help_icon)
	lateinit var ivMenuHelp: ImageView

  @BindView(R.id.progress_enter_url)
	lateinit var pbEnterUrl: ProgressBar

	private lateinit var mFirstTimeGuestActionListener: FirstTimeGuestActionListener
	private var mEnterUrlListener: EnterUrlListener? = null
	private var mIsFirstTimeGuestFlow = false
	private var mDetail: Detail? = null

	/**
	 * Init enter url fragment.
	 *
	 * @param enterUrlListener the enter url listener
	 */
	fun initEnterUrlFragment(enterUrlListener: EnterUrlListener) {
		mEnterUrlListener = enterUrlListener
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		NewRelic.setInteractionName(Interactions.ENTER_URL_SEARCH.interaction)
		var view: View? = null
		try {
			val bundle = arguments
			if (bundle != null) {
				mIsFirstTimeGuestFlow = bundle.getBoolean(AppConstants.KEY_ENTER_URL_FOR_NEW_GUEST)
			}
			if (mIsFirstTimeGuestFlow) {
				view = inflater.inflate(R.layout.fragment_guest_enter_url, container, false)
				mFirstTimeGuestActionListener = activity as FirstTimeGuestActionListener
			} else {
				view = inflater.inflate(R.layout.fragment_enter_url, container, false)
			}
			if (view != null) {
				ButterKnife.bind(this, view)
			}
			edEnterUrlText.setText(AppConstants.WEBVIEW_WEBPAGE_LINK_HTTPS)
			edEnterUrlText.setSelection(AppConstants.WEBVIEW_WEBPAGE_LINK_HTTPS.length)
			registerCrossButtonClickListener()
			registerTextChangeListener()
			if (mIsFirstTimeGuestFlow && view != null) {
				handleBackActionForGuest(view)
			}
		} catch (ex: Exception) {
			mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.ENTERURL,
					"EnterUrlFragment onCreateView() - Got error displaying enterurl fragment", ex,
					null, true, false)
		}
		return view
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
	}

	/**
	 * On join clicked.
	 */
	@OnClick(R.id.btn_enter_url_join_meeting)
	fun onJoinClicked() {
		mLogger.record(LogEvent.FEATURE_JOINFROMSEARCH)
		joinAudioMeeting()
		CommonUtils.hideKeyboard(activity)
	}

	/**
	 * On help icon clicked.
	 */
	@OnClick(R.id.menu_help_icon)
	fun onHelpIconClicked() {
		mLogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.ENTERURL, "EnterUrlFragment " +
				"onHelpIconClicked() - Showing Help Dialog", null, null, true, false)
		NewRelic.incrementAttribute("OnBoardingHelpOnEnterUrlFragment")
		val alertDialog = AlertDialog.Builder(activity)
				.setTitle(getString(R.string.trouble_joining_title))
				.setMessage(getString(R.string.support_msg_on_meeting_join))
				.setPositiveButton(R.string.dialog_ok) { dialog, which -> dialog.dismiss() }
				.setNegativeButton(R.string.action_contact_support, object : DialogInterface.OnClickListener {
					var appSubject = resources.getString(R.string.help_app_subject)
					var appVersion = resources.getString(R.string.app_version)
					var opSystemVersion = resources.getString(R.string.os_version)
					var deviceModels = resources.getString(R.string.device_model)
					override fun onClick(dialog: DialogInterface, which: Int) {
						val buildVersion = CommonUtils.getAppVersion(WeakReference(activity))
						val osVersion = Build.VERSION.RELEASE
						val deviceModel = CommonUtils.getDeviceModelName()
						NewRelic.incrementAttribute(LogEvent.FEATURE_HELP.eventName)
						ElkTransactionIDUtils.resetTransactionId()
						val customerCare: Array<String>
						customerCare = if (CommonUtils.isUsersLocaleJapan()) {
							arrayOf(AppConstants.HELP_APP_TO_EMAIL_JA)
						} else {
							arrayOf(SharedPreferencesManager.getInstance().supportEmail)
						}
						val activity = activity as BaseActivity?
						activity?.openEmailApp(WeakReference(activity), customerCare, appSubject,
								AppConstants.NEW_LINE_CHARACTER + appVersion + AppConstants.COLON +
										AppConstants.BLANK_SPACE + buildVersion + AppConstants.NEW_LINE_CHARACTER +
										opSystemVersion + AppConstants.COLON + AppConstants.BLANK_SPACE + osVersion +
										AppConstants.NEW_LINE_CHARACTER + deviceModels + AppConstants.COLON +
										AppConstants.BLANK_SPACE + deviceModel)
						dialog.dismiss()
					}
				}).create()
		alertDialog.setOnShowListener {
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(AppConstants.COLOR_CODE_ACTION_BLUE)
			alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(AppConstants.COLOR_CODE_ACTION_GREY)
		}
		alertDialog.show()
	}

	/**
	 * On skip clicked.
	 */
	@OnClick(R.id.img_skip_btn)
	fun onSkipClicked() {
		closeFragment(false)
		mFirstTimeGuestActionListener.onEnterUrlFragmentSkipped()
	}

	override fun showProgress() {
		btnEnterUrl.setText(null)
		edEnterUrlText.isEnabled = false
		pbEnterUrl.visibility = View.VISIBLE
		ivMenuHelp.visibility = View.GONE
		mEnterUrlListener?.toggleSwipeAndClickTabs(false)
	}

	override fun hideProgress() {
		btnEnterUrl.text = getString(R.string.btn_title_join)
		edEnterUrlText.isEnabled = true
		pbEnterUrl.visibility = View.GONE
		ivMenuHelp.visibility = View.VISIBLE
		mEnterUrlListener?.toggleSwipeAndClickTabs(true)
	}

	override fun closeFragment(joinMeeting: Boolean) {
			activity?.supportFragmentManager?.popBackStack()
		if (joinMeeting) {
			mDetail?.let {
				mFirstTimeGuestActionListener.onJoinMeetingCalled(it)
			}
		}
	}

	override fun sendToLoginPage() {
		val intent = Intent(activity, OnBoardAuthActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		startActivity(intent)
		activity?.finish()
	}

	override fun launchAudioSelectionScreen(detail: Detail) {
		mDetail = detail
		if (activity != null && isAdded) {
			hideProgress()
			if (activity is OnBoardAuthActivity) {
				val onBoardAuthActivity = activity as OnBoardAuthActivity
				if (onBoardAuthActivity.isAppEnterForeground) {
					if (mIsFirstTimeGuestFlow) {
						closeFragment(true)
					}
				}
			}
		}
	}

	/**
	 * Adding TextChangeListener on EntryURLText to correct the input text if user enter http twice.
	 */
	private fun registerTextChangeListener() {
		edEnterUrlText.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				val text = edEnterUrlText.text.toString()
				setLeftRightDrawables(text)
				if (CommonUtils.isMultipleHttpOccurrence(text)) {
					edEnterUrlText.setText(CommonUtils.replaceMultipleOccurrenceHttp(text))
					edEnterUrlText.setSelection(edEnterUrlText.text.length)
				}
			}

			override fun afterTextChanged(s: Editable) {}
		})
	}

	/**
	 * Adding web and cross icon on EntryURLText at left and right side
	 *
	 * @param text entry url text
	 */
	private fun setLeftRightDrawables(text: String) {
		edEnterUrlText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
	}

	/**
	 * Join audio meeting.
	 */
	fun joinAudioMeeting() {
		val searchData = edEnterUrlText.text.toString().trim { it <= ' ' }
		if (searchData.length > 0) {
			if (tvInvalidUrl.visibility == View.VISIBLE) {
				tvInvalidUrl.visibility = View.INVISIBLE
			}
			showProgress()
			val elasticSearchRepository = INSTANCE
			val result = elasticSearchRepository.getMeetingRoomInfoFromFurl(searchData, false).subscribe({ result: SearchResult ->
				ClientInfoResultCache.getInstance().selectedMeetingRoomId = result.meetingRoomId
				val detail = Detail()
				detail.useHtml5 = result.useHtml5
				detail.conferenceId = result.conferenceId
				detail.meetingUrl = result.furl
				detail.searchResult = result
				launchAudioSelectionScreen(detail)
			}
		) { error: Throwable? ->
				val msg = "Invalid meeting url =$searchData"
				mLogger.error(TAG, LogEvent.API_GMSEARCH, LogEventValue.ENTERURL, msg, null, null, false, false)
				onEnterUrlError()
			}
		}
	}

	override fun showInvalidUrlAlert() {
		tvInvalidUrl.visibility = View.VISIBLE
	}

	override fun notifyAdapterOnEnterUrlSearchUpdate(results: List<Result>) {
		if (results.isNotEmpty()) {
			showInvalidUrlAlert()
		}
	}

	override fun onEnterUrlError() {
		if (activity != null && isAdded) {
			hideProgress()
			if (activity is OnBoardAuthActivity) {
				val onBoardAuthActivity = activity as OnBoardAuthActivity
				if (onBoardAuthActivity.isAppEnterForeground) {
					showErrorMessage()
				}
			}
		}
	}

	/**
	 * Making decision for retry to fetch the refresh token or
	 * showing alert dialog if provided input URL is invalid.
	 *
	 */
	private fun showErrorMessage() {
		showInvalidUrlAlert()
	}
	/**
	 * Requesting to network to fetch refresh token and checking the max limit of network attempt to fetch the token.
	 */
	/**
	 * Adding click listener for SearchBox right drawable cross button.
	 * Clearing the EnterURLText on cross button click.
	 */
	@SuppressLint("ClickableViewAccessibility")
	private fun registerCrossButtonClickListener() {
		edEnterUrlText.setOnTouchListener(OnTouchListener { v, event -> //DRAWABLE_LEFT = 0; DRAWABLE_TOP = 1; DRAWABLE_BOTTOM = 3;
			val DRAWABLE_RIGHT = 2
			if (event.action == MotionEvent.ACTION_UP) {
				if (edEnterUrlText.compoundDrawables[DRAWABLE_RIGHT] != null) {
					if (event.rawX >= edEnterUrlText.right -
							edEnterUrlText.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
						edEnterUrlText.setText(null)
						return@OnTouchListener false
					}
				}
			}
			false
		})
	}

	override fun onRecentMeetingClick(item: ImeetingRoomInfo) {}
	override fun onResume() {
		super.onResume()
		if (mIsFirstTimeGuestFlow) {
			SharedPreferencesManager.getInstance().firstTimeGuestUser(false)
		}
	}

	/**
	 * Handling back button onClick for Guest user who loged-in first time.
	 *
	 * @param view the view object
	 */
	private fun handleBackActionForGuest(view: View) {
		view.isFocusableInTouchMode = true
		view.requestFocus()
		view.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				mFirstTimeGuestActionListener.onBackClicked()
				return@OnKeyListener true
			}
			false
		})
	}

	companion object {
		private val TAG = EnterUrlFragment::class.java.simpleName
		private const val sNoOfRetries = 0
	}
}
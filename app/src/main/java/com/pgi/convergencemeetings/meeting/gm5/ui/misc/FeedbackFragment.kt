package com.pgi.convergencemeetings.meeting.gm5.ui.misc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.constants.AppConstants.POSITIVE
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.AppUpgrade.Companion.doUpdateCheck
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.home.ui.AppBaseLayoutActivity
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.utils.ConferenceManager
import com.pgi.logging.Logger
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault

/**
 * Created by visheshchandra on 12/11/2017.
 */
@ObsoleteCoroutinesApi
@UnstableDefault
class FeedbackFragment : Fragment() {
	private val logger: Logger = CoreApplication.mLogger
	private val TAG = FeedbackFragment::class.java.simpleName
	var mPositiveFeedClicked = false
	var mNegativeFeedClicked = false

	@BindView(R.id.btn_positive_feedback)
	lateinit var btnPositiveFeedback: Button

	@BindView(R.id.btn_negative_feedback)
	lateinit var btnNegativeFeedback: Button

	@BindView(R.id.textSkip)
	lateinit var textSkip: TextView

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		NewRelic.setInteractionName(Interactions.FEEDBACK.interaction)
		val view = inflater.inflate(R.layout.fragment_feedback, container, false)
		ButterKnife.bind(this, view)
		view.isFocusableInTouchMode = true
		view.requestFocus()
		view.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
			if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
				onPositiveFeedClick()
				return@OnKeyListener true
			}
			false
		})
		val activity: Activity? = activity
		activity?.let { doUpdateCheck(it) }
		return view
	}

	@OnClick(R.id.btn_positive_feedback)
	fun onPositiveFeedClick() {
		moveToLastFragment()
		sendFeedbackEventLog(POSITIVE)
	}

	 fun sendFeedbackEventLog(feedbackRating: String) {
		if(!logger.meetingModel.uniqueMeetingId.isNullOrEmpty()) {
			logger.mixPanelEndOfMeetingFeedback.rating = feedbackRating
			logger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_END_OF_MEETING_FEEDBACK, AppConstants.MIXPANEL_EVENT  + LogEventValue.MIXPANEL_END_OF_MEETING_FEEDBACK.value,
					null, null, false, true)
			logger.meetingModel.uniqueMeetingId = null
			logger.clearModels()
		}
	}

	fun moveToLastFragment() {
		btnNegativeFeedback.isEnabled = false
		mPositiveFeedClicked = true
		mNegativeFeedClicked = false
		if (!ConferenceManager.isMeetingActive()) {
			//Need to handle the first time guest user flow to show app permission screen...
			val firstTimeGuestUserForThankYou = SharedPreferencesManager.getInstance().isFirstTimeGuestUserForThankYou
			if (firstTimeGuestUserForThankYou) {
				SharedPreferencesManager.getInstance().firstTimeGuestUserForThankYou(false)
			}
			launchApplicationFlow(firstTimeGuestUserForThankYou)
			activity?.finish()
		} else {
			btnNegativeFeedback.visibility = View.INVISIBLE
			btnPositiveFeedback.visibility = View.INVISIBLE
			textSkip.visibility = View.INVISIBLE
		}
	}

	private fun launchApplicationFlow(firstTimeGuestUserForThankYou: Boolean) {
		val navigationIntent = Intent()
		activity?.let { navigationIntent.setClass(it, AppBaseLayoutActivity::class.java) }
		if (firstTimeGuestUserForThankYou) navigationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP else navigationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
		startActivity(navigationIntent)
	}

	@OnClick(R.id.btn_negative_feedback)
	fun onNegativeFeedClick() {
		(activity as? WebMeetingActivity)?.openFeedbackCommentFragment()
	}

	@OnClick(R.id.textSkip)
	fun onSkipClick() {
		moveToLastFragment()
		logger.clearModels()
	}

	fun takeClickAction() {
		if (mPositiveFeedClicked) {
			onPositiveFeedClick()
		} else {
			onNegativeFeedClick()
		}
	}
}
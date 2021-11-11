package com.pgi.convergencemeetings.base.services.retry_callbacks

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.base.ui.retry.Fragments.showDialogAllowingStateLoss
import com.pgi.convergencemeetings.base.ui.retry.SimpleDialogFragment.Companion.newInstance
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue

/**
 * Created by nnennaiheke on 3/14/18.
 */
/**
 * This class handles the what the UI will display upon the retry failures we may experience in our app. There are some scenarios where we do not
 * give the user any information, and others where we show either a toast message regarding the faiure or an alert
 * prompting the user to choose re-joining the meeting or canceling the session altoghther
 */
class RetryCallbacks(private val mContext: Context, fragmentManager: BaseActivity) {
	private val fragmentManager: FragmentManager
	fun onServiceRetryFailed(retryStatus: RetryStatus) {
		val value = retryStatus.value()
		val reason = retryStatus.reason()
		// since this event has multiple params we should use a custom event not just a name
		if (value >= 0) {
			var titleResId = 0
			var messageResId = 0
			var tag = AppConstants.EMPTY_STRING
			when (value) {
				RetryStatus.FAILED -> {
				}
				RetryStatus.JOIN_MEETING -> {
					messageResId = R.string.failed_to_join_meeting
				}
				RetryStatus.START_MEETING -> messageResId = R.string.failed_to_start_meeting
				RetryStatus.WS_CLIENT_INFO_SELF -> messageResId = R.string.failed_to_join_meeting
				RetryStatus.WS_CLIENT_INFO_OTHERS -> messageResId = R.string.failed_to_join_meeting
				RetryStatus.UAPI_SESSION -> messageResId = R.string.failed_to_join_meeting
				RetryStatus.UAPI_PARTICIPANT_JOIN_MEETING -> {
					messageResId = R.string.failed_to_join_meeting
				}
				RetryStatus.UAPI_ROOM_INFO -> {
					messageResId = R.string.failed_to_join_meeting
				}
				RetryStatus.WS_LAST_USED_PHONE -> {
				}
				RetryStatus.WS_GET_PHONE_NUMBERS -> {
				}
				RetryStatus.UAPI_DIALOUT -> messageResId = R.string.failed_to_dial_out
				RetryStatus.WS_MEETING_ROOM_SEARCH -> messageResId = R.string.failed_to_complete_search
				RetryStatus.WS_ENTERPRISE_SEARCH -> messageResId = R.string.failed_to_complete_search
				RetryStatus.UAPI_MEETING_EVENTS -> {
					titleResId = R.string.failed_to_get_meeting_events_head
					messageResId = R.string.failed_to_get_meeting_events_body
					tag = TAG_DIALOG_MEETING_REJOIN
				}
				RetryStatus.PIA_MEETING_EVENTS -> {
					titleResId = R.string.failed_to_get_meeting_events_head
					messageResId = R.string.failed_to_get_meeting_events_body
					tag = TAG_DIALOG_MEETING_REJOIN
				}
				RetryStatus.UAPI_END_MEETING -> {
				}
				RetryStatus.UAPI_LEAVE_MEETING -> messageResId = R.string.failed_to_leave_meeting
				RetryStatus.UAPI_MUTE_PARTICIPANT -> messageResId = R.string.failed_to_mute
				RetryStatus.UAPI_DISMISS_AUDIO_PARTICIPANT -> messageResId = R.string.failed_to_dismiss
				RetryStatus.UAPI_DEMOTE_PARTICIPANT -> messageResId = R.string.failed_to_demote
				RetryStatus.UAPI_PROMOTE_PARTICIPANT -> messageResId = R.string.failed_to_promote
				RetryStatus.PIA_SUBSCRIBE -> messageResId = R.string.failed_to_join_meeting
				RetryStatus.PIA_GET_CONFERENCE_STATE -> {
				}
				RetryStatus.PIA_UNSUSCRIBE -> {
				}
				RetryStatus.PIA_SET_CONFERENCE_WATCH -> {
				}
				RetryStatus.PIA_START_CONFERENCE -> messageResId = R.string.failed_to_start_meeting
				RetryStatus.PIA_SET_CONFERENCE_OPTION -> {
				}
				RetryStatus.PIA_SET_PARTICIPANT_OPTION -> messageResId = R.string.failed_to_set_participant_option
				RetryStatus.PIA_HANGUP -> {
				}
				RetryStatus.PIA_END_CONFERENCE -> {
				}
				RetryStatus.PIA_CLEAR_CONFERENCE_WATCH -> {
				}
				RetryStatus.PIA_DIALOUT -> messageResId = R.string.failed_to_dial_out
				RetryStatus.PIA_SET_PARTICIPANT_INFO -> {
				}
				else -> {
				}
			}
			if (titleResId != 0 && messageResId != 0 && !TextUtils.isEmpty(tag)) {
				showSimpleDialog(titleResId
						, messageResId
						, tag)
				val msg = mContext.getString(messageResId)
				CoreApplication.mLogger.error(TAG, LogEvent.ERROR, LogEventValue.DISCONNECTED_UNEXPECTED, msg, null, null, true, false)
			} else if (messageResId != 0 && TextUtils.isEmpty(tag)) {
				showSimpleToast(messageResId)
				val msg = mContext.getString(messageResId)
				CoreApplication.mLogger.error(TAG, LogEvent.ERROR, LogEventValue.DISCONNECTED_UNEXPECTED, msg, null, null, true, false)
			} else {
				logSimpleFailure(value, reason)
			}
		}
	}

	private fun showSimpleDialog(titleRes: Int, messageRes: Int, tag: String) {
		showDialogAllowingStateLoss(fragmentManager, newInstance(titleRes, messageRes), tag)
	}

	private fun showSimpleToast(messageRes: Int) {
		Toast.makeText(mContext, mContext.resources.getString(messageRes), Toast.LENGTH_LONG).show()
	}

	private fun logSimpleFailure(failure: Int, reason: Int) {
		Log.v(failure.toString(), reason.toString())
	}

	companion object {
		private val TAG = RetryCallbacks::class.java.simpleName
		private const val TAG_DIALOG_MEETING_REJOIN = "TAG_DIALOG_MEETING_REJOIN"
	}

	init {
		this.fragmentManager = fragmentManager.supportFragmentManager
	}
}
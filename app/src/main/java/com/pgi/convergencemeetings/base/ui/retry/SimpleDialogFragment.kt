package com.pgi.convergencemeetings.base.ui.retry

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.home.ui.AppBaseLayoutActivity
import com.pgi.convergencemeetings.meeting.BaseMeetingActivity
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault

/**
 * Created by nnennaiheke on 3/14/18.
 */
@ObsoleteCoroutinesApi
@UnstableDefault
class SimpleDialogFragment : DialogFragment(), RetryFailedContactor.fragment {

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val arguments = arguments
		var title: String? = AppConstants.EMPTY_STRING
		if (arguments != null && arguments.containsKey(ARG_TITLE)) {
			title = arguments.getString(ARG_TITLE)
		} else if (arguments != null && arguments.containsKey(ARG_TITLE_RES)) {
			title = getString(arguments.getInt(ARG_TITLE_RES, 0))
		}
		var message: String? = AppConstants.EMPTY_STRING
		if (arguments != null && arguments.containsKey(ARG_MESSAGE)) {
			message = arguments.getString(ARG_MESSAGE)
		} else if (arguments != null && arguments.containsKey(ARG_MESSAGE_RES)) {
			message = getString(arguments.getInt(ARG_MESSAGE_RES, 0))
		}
		val dialog = AlertDialog.Builder(activity, R.style.DialogTheme)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(R.string.failed_to_get_meeting_events_positive) { dialog, id -> sendUserToRejoinMeeting() }
				.setNegativeButton(R.string.failed_to_get_meeting_events_negative) { dialog, id -> // if this button is clicked, close
					// current activity
					val intent = Intent(context, AppBaseLayoutActivity::class.java)
					val bundle = Bundle()
					bundle.putBoolean(AppConstants.KEY_JOIN_RETRY_FAILED, true)
					intent.putExtras(bundle)
					startActivity(intent, bundle)
					dialog.dismiss()
				}
				.setInverseBackgroundForced(true)
				.setCancelable(false)
				.show()
		return dialog
	}

	override fun sendUserToRejoinMeeting() {
		val baseMeetingActivity = activity as BaseMeetingActivity?
		baseMeetingActivity?.sendUserToRejoinMeeting()
	}

	companion object {
		private const val ARG_TITLE_RES = "arg_TitleRes"
		private const val ARG_MESSAGE_RES = "arg_MessageRes"
		private const val ARG_TITLE = "arg_Title"
		private const val ARG_MESSAGE = "arg_Message"
		fun newInstance(title: String?, message: String?): SimpleDialogFragment {
			val fragment = SimpleDialogFragment()
			val arguments = Bundle()
			arguments.putString(ARG_TITLE, title)
			arguments.putString(ARG_MESSAGE, message)
			fragment.arguments = arguments
			return fragment
		}

		@JvmStatic
    fun newInstance(titleRes: Int, messageRes: Int): SimpleDialogFragment {
			val fragment = SimpleDialogFragment()
			val arguments = Bundle()
			arguments.putInt(ARG_TITLE_RES, titleRes)
			arguments.putInt(ARG_MESSAGE_RES, messageRes)
			fragment.arguments = arguments
			return fragment
		}
	}
}
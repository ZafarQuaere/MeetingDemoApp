package com.pgi.convergencemeetings.meeting.gm5.ui.misc

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.pgi.convergence.application.CoreApplication.Companion.mLogger
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.UserFlowStatus
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue

class MiscRoomViewFragment : Fragment() {

	@BindView(R.id.tv_waiting_room_header)
	lateinit var tvWaitingRoomHeader: TextView

	@BindView(R.id.lo_wait)
	lateinit var lo_wait: RelativeLayout

	@BindView(R.id.lo_denied)
	lateinit var lo_denied: RelativeLayout

	@BindView(R.id.lo_full)
	lateinit var lo_full: RelativeLayout

	@BindView(R.id.lo_ended)
	lateinit var lo_ended: RelativeLayout

	@BindView(R.id.tv_waiting_room_title_full)
	lateinit var tvWaitingRoomTitleFull: TextView

	@BindView(R.id.tv_waiting_room_title_locked)
	lateinit var tvWaitingRoomTitleLocked: TextView

	@BindView(R.id.tv_waiting_room_title_name)
	lateinit var tvWaitingRoomTitleName: TextView

	@BindView(R.id.tv_waiting_room_meeting_ended_title)
	lateinit var tvWaitingRoomEndTitle: TextView

	@BindView(R.id.lo_locked)
	lateinit var lo_locked: RelativeLayout
	internal var mFirstName: String? = null
	internal var mStatus: String? = null

	lateinit var meetingUserViewModel: MeetingUserViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mFirstName = arguments?.getString("mFirstName")
		mStatus = arguments?.getString("mStatus")
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.waiting_room_layout, container, false)
		ButterKnife.bind(this, view)
		activity?.let {
			meetingUserViewModel = ViewModelProviders.of(it).get(MeetingUserViewModel::class.java)
		}
		return view
	}

	override fun onResume() {
		initTitle()
		mStatus?.let { setWaitScreen(it) }
		super.onResume()
	}

	fun initTitle() {
		val res = resources
		if (mFirstName != null) {
			@SuppressLint("StringFormatInvalid", "LocalSuppress")
			val waitingRoomHeader = String.format(res.getString(R.string.waiting_room_meeting_name), CommonUtils.formatCamelCase(mFirstName) + "'s")
			tvWaitingRoomHeader.text = Html.fromHtml(waitingRoomHeader)
		} else {
			tvWaitingRoomHeader.text = Html.fromHtml(res.getString(R.string.waiting_room_meeting_name))
		}
	}

	internal fun setWaitScreen(status: String) {
		when (status) {
			UserFlowStatus.JOIN_WAIT_ROOM.status -> showWaitingRoomView()
			UserFlowStatus.DISMISSED_LOCK.status,
			UserFlowStatus.JOIN_LOCK_MEETING.status -> showMeetingLockedView()
			UserFlowStatus.DISMISSED_ROOM_AT_CAPACITY.status,
			UserFlowStatus.JOIN_MEETING_AT_CAPACITY.status -> showMeetingFullView()
			UserFlowStatus.DISMISSED_BY_HOST.status,
			UserFlowStatus.DISMISSED_WAIT_TIMEOUT_HOST.status,
			UserFlowStatus.DISMISSED_WAIT_TIMEOUT_ADMIT.status -> showMeetingWaitDeniedView()
			UserFlowStatus.DISMISSED_MEETING_ENDED.status -> showMeetingEndedView()
		}
	}

	fun showMeetingFullView() {
		val res = resources
		lo_full.visibility = View.VISIBLE
		lo_denied.visibility = View.GONE
		lo_locked.visibility = View.GONE
		lo_wait.visibility = View.GONE
		lo_ended.visibility = View.GONE
		@SuppressLint("StringFormatInvalid", "LocalSuppress")
		val waitingRoomTitle = String.format(getString(R.string.meeting_full), CommonUtils.formatCamelCase(mFirstName) + "'s")
		tvWaitingRoomTitleFull.text = Html.fromHtml(waitingRoomTitle)
		mLogger.info(
				TAG, LogEvent.API_UAPI, LogEventValue.JOIN_MEETING_AT_CAPACITY,
				"showMeetingFullView(): Showing Meeting at Capacity Screen", null,
				null, true, true)
	}

	fun showMeetingLockedView() {
		val res = resources
		lo_locked.visibility = View.VISIBLE
		lo_denied.visibility = View.GONE
		lo_full.visibility = View.GONE
		lo_wait.visibility = View.GONE
		lo_ended.visibility = View.GONE
		@SuppressLint("StringFormatInvalid", "LocalSuppress")
		val waitingRoomTitle = String.format(getString(R.string.meeting_locked), CommonUtils.formatCamelCase(mFirstName) + "'s")
		tvWaitingRoomTitleLocked.text = Html.fromHtml(waitingRoomTitle)
		mLogger.info(
				TAG, LogEvent.API_UAPI, LogEventValue.JOIN_LOCK_MEETING,
				"showMeetingLockedView(): Showing Meeting Lock Screen", null,
				null, true, true)
	}

	fun showMeetingWaitDeniedView() {
		lo_denied.visibility = View.VISIBLE
		lo_locked.visibility = View.GONE
		lo_full.visibility = View.GONE
		lo_wait.visibility = View.GONE
		lo_ended.visibility = View.GONE
		mLogger.info(
				TAG, LogEvent.API_UAPI, LogEventValue.DISMISSED_BY_HOST,
				"showMeetingWaitDeniedView(): Showing Waiting room denied screen", null,
				null, true, true)
	}

	fun showMeetingEndedView() {
		lo_denied.visibility = View.GONE
		lo_locked.visibility = View.GONE
		lo_full.visibility = View.GONE
		lo_wait.visibility = View.GONE
		lo_ended.visibility = View.VISIBLE
		@SuppressLint("StringFormatInvalid", "LocalSuppress")
		val endedRoomTitle = String.format(getString(R.string.meeting_ended_wait_state), CommonUtils
				.formatCamelCase(mFirstName) + "'s")
		tvWaitingRoomEndTitle.text = Html.fromHtml(String.format(endedRoomTitle))
		mLogger.info(
				TAG, LogEvent.API_UAPI, LogEventValue.MEETING_ENDED_WHILE_WAITING,
				"showMeetingEndedView(): Showing Meeting Ended Screen while in waiting room",
				null, null, true, true)
	}

	fun showWaitingRoomView() {
		val res = resources
		lo_wait.visibility = View.VISIBLE
		lo_denied.visibility = View.GONE
		lo_full.visibility = View.GONE
		lo_locked.visibility = View.GONE
		lo_ended.visibility = View.GONE
		@SuppressLint("StringFormatInvalid", "LocalSuppress")
		val waitingRoomTitle = String.format(getString(R.string.waiting_room_meeting_name), CommonUtils.formatCamelCase(mFirstName) + "'s")
		tvWaitingRoomTitleName.text = Html.fromHtml(waitingRoomTitle)
		mLogger.info(
				TAG, LogEvent.JOIN_WAIT_ROOM, LogEventValue.JOIN_WAIT_ROOM, LogEventValue.JOIN_WAIT_ROOM.value, null,
				null, true, true)
	}

	@OnClick(R.id.waiting_back_btn)
	fun onWaitingBackBtn() {
		//        below code has been added to call leave api when user is in waiting room
		//        and clicks on back button , so that it should update on server that user has left the waiting room see SSMOBILE-4877 for more ref.
		mStatus?.let { status ->
			if (mStatus == UserFlowStatus.JOIN_WAIT_ROOM.status) {
//               Make userInMeeting to true since for waiting room it is false.
				meetingUserViewModel?.userInMeeting = true
				meetingUserViewModel?.userIsInWaitingRoom = true
				meetingUserViewModel?.leaveMeeting()
//              Providing delay so that api call gets completed before the finish of activity
				Handler().postDelayed({
					activity?.finish()
				}, 1000)
				return
			}
		}
		activity?.finish()
	}

	companion object {
		private val TAG = MiscRoomViewFragment::class.java.simpleName

		@JvmStatic
		fun newInstance(firstName: String?, mStatus: String?): MiscRoomViewFragment {
			val joinWaitingRoomFragment = MiscRoomViewFragment()
			val args = Bundle()
			args.putString("mFirstName", firstName)
			args.putString("mStatus", mStatus)
			joinWaitingRoomFragment.arguments = args
			return joinWaitingRoomFragment
		}
	}
}
package com.pgi.convergencemeetings.meeting.gm5.ui.security

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.WaitingRoomEvents
import com.pgi.convergence.utils.InternetConnection
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.UserFlowStatus
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.meeting.gm5.ui.view.MeetingSecurityHeader
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import kotlin.collections.ArrayList

class MeetingSecurityFragment: Fragment() {

    @BindView(R.id.rv_meeting_security)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.internet_reconnecting_banner_layout)
    lateinit var mInternetReconnectingBannerLayout: ViewGroup

    private var mLinearLayoutManager: LinearLayoutManager? = null
    private val mUserList: MutableList<User> = ArrayList()
    private var mMeetingSecurityAdapter: MeetingSecurityMenuAdapter? = null
    lateinit var mMeetingSecurityHeader: MeetingSecurityHeader
    protected var mLogger: Logger = CoreApplication.mLogger

    var mMeetingEventsModel: MeetingEventViewModel? = null
    var mMeetingUserViewModel: MeetingUserViewModel? = null

    private var isDelegateHost = false
    private var mIsMeetingHost = false
    var isWaitingRoomEnabledLocked = false
        private set
    var isFrictionFreeEnabledLocked = false
        private set
    var numGuestsAdmitted = 0
    var numGuestsDenied = 0
    var guestAdmitted : MutableList<String> = ArrayList()
    var guestDenied : MutableList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.meeting_security_layout, container, false)
        ButterKnife.bind(this, view)
        mLinearLayoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = mLinearLayoutManager
        mMeetingSecurityHeader = MeetingSecurityHeader(view.context)
        registerViewModelListener()
        updateUIForRoomRole()
        sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.OPEN_MEETING_SECURITY)
        onUpdateInternetAvailability(InternetConnection.isConnected(context))
        mMeetingSecurityHeader.tvAdmitAll.setOnClickListener {
            admitAllUsers()
        }

        mMeetingSecurityHeader.restrictSharingView.swSecurityOption.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (compoundButton.isPressed) {
                sendMixpanelWaitingRoomControlEvent(if(isChecked) WaitingRoomEvents.ENABLE_RESTRICT_SHARING else WaitingRoomEvents.DISABLE_RESTRICT_SHARING)
                mMeetingUserViewModel?.toggleFrictionFree(!isChecked)
            }
        }

        mMeetingSecurityHeader.waitingRoomView.swSecurityOption.setOnCheckedChangeListener { buttonview, isChecked ->
            if(buttonview.isPressed) {
                sendMixpanelWaitingRoomControlEvent(if (isChecked) WaitingRoomEvents.ENABLE_WAITING_ROOM else WaitingRoomEvents.DISABLE_WAITING_ROOM)
                mMeetingUserViewModel?.toggleWaitingRoom(isChecked)
            }
        }
        mMeetingSecurityHeader.lockRoomView.swSecurityOption.setOnCheckedChangeListener { buttonview, isChecked ->
            if (buttonview.isPressed) {
                sendMixpanelWaitingRoomControlEvent(if (isChecked) WaitingRoomEvents.LOCK_MEETING else WaitingRoomEvents.UNLOCK_MEETING)
                mMeetingUserViewModel?.lockUnlockMeeting(isChecked)
            }
        }

        mMeetingSecurityAdapter = MeetingSecurityMenuAdapter(view.context, CoreApplication.mLogger, mMeetingSecurityHeader,mUserList)
        recyclerView.adapter = mMeetingSecurityAdapter

        mMeetingSecurityAdapter?.onAdmitDenyUser = ::onAdmitDenyUser

        sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.OPEN_MEETING_SECURITY)

        return view
    }

    fun onAdmitDenyUser(user: User, isAdmitted: Boolean) {
        if (user.isInAudioWaitingRoom) {
            updateAdmitDenyCount(user, isAdmitted)
            mMeetingUserViewModel?.updateAudioUserAdmitDeny(user, isAdmitted)
        } else {
            updateAdmitDenyCount(user, isAdmitted)
            mMeetingUserViewModel?.updateUserAdmitDeny(user, isAdmitted)
        }
    }

    private fun updateAdmitDenyCount(user: User, isAdmitted: Boolean) {
        var eventName = WaitingRoomEvents.ADMIT
        if (isAdmitted) {
            numGuestsAdmitted += 1
            guestAdmitted.add(user.email.toString())
        } else {
            numGuestsDenied += 1
            guestDenied.add(user.email.toString())
            eventName = WaitingRoomEvents.DENY
        }
        sendMixpanelWaitingRoomControlEvent(eventName)
    }

    fun admitAllUsers() {
        mUserList.forEach { user ->
            if (user.isInAudioWaitingRoom) {
                mMeetingUserViewModel?.updateAudioUserAdmitDeny(user, isAdmit = true)
                numGuestsAdmitted += 1
                guestAdmitted.add(user.initials.toString() + user.firstName.toString())
            } else {
                mMeetingUserViewModel?.updateUserAdmitDeny(user, admit = true)
                numGuestsAdmitted += 1
                guestAdmitted.add(user.email.toString())
            }
        }
        sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.ADMIT_ALL)
    }

    private fun registerViewModelListener() {
        activity?.let {
            mMeetingEventsModel = ViewModelProviders.of(it).get(MeetingEventViewModel::class.java)
            mMeetingUserViewModel = ViewModelProviders.of(it).get(MeetingUserViewModel::class.java)
        }
        mMeetingUserViewModel?.apply {
            userFlowStatus.observe(this@MeetingSecurityFragment, Observer { userFlowStatus: UserFlowStatus -> respondToUserFlowStatusUpdate(userFlowStatus) })
            guestWaitingList.observe(this@MeetingSecurityFragment, androidx.lifecycle.Observer {  users: List<User> -> respondToWaitingListUpdate(users) })
            meetingLockStatus.observe(this@MeetingSecurityFragment, androidx.lifecycle.Observer { enabled: Boolean -> respondToLockedRoomState(enabled) })
            getRoomInfoResponse()?.let {
                isWaitingRoomEnabledLocked = it.waitingRoom.enabledLocked
                isFrictionFreeEnabledLocked = it.frictionFree.enabledLocked
            }
            authResponse?.let {
                mIsMeetingHost = it.roomRole.toLowerCase() == AppConstants.HOST.toLowerCase()
            }
        }
        mMeetingEventsModel?.apply {
            frictionFreeEnabledStatus.observe(this@MeetingSecurityFragment, androidx.lifecycle.Observer { enabled: Boolean -> respondToFrictionFreeStatus(enabled) })
            userFlowStatus.observe(this@MeetingSecurityFragment, Observer { userFlowStatus: UserFlowStatus -> respondToUserFlowStatusUpdate(userFlowStatus) })
            waitingRoomEnabledStatus.observe(this@MeetingSecurityFragment, androidx.lifecycle.Observer { enabled: Boolean -> respondToWaitingRoomState(enabled) })
            guestWaitingList.observe(this@MeetingSecurityFragment, androidx.lifecycle.Observer {  users: List<User> -> respondToWaitingListUpdate(users) })
            meetingLockStatus.observe(this@MeetingSecurityFragment, androidx.lifecycle.Observer { enabled: Boolean -> respondToLockedRoomState(enabled) })
            users.observe(this@MeetingSecurityFragment, Observer { updateUIForRoomRole() })
        }
        isDelegateHost = mMeetingEventsModel?.getCurrentUser()?.delegateRole ?: false
   }

    @OnClick(R.id.meeting_security_back_btn)
    fun onMeetingSecurityBackBtn() {
        sendMixpanelWaitingRoomControlEvent(WaitingRoomEvents.CANCEL)
        (activity as? WebMeetingActivity)?.resumeActivity()
    }

    private fun respondToLockedRoomState(enabled: Boolean) {
        mMeetingSecurityHeader.lockRoomView.swSecurityOption.isChecked = enabled
        if (enabled) {
            mMeetingSecurityHeader.waitingRoomView.alpha = AppConstants.ALPHA_WAITINGROOM_DISABLED
            mMeetingSecurityHeader.waitingRoomView.swSecurityOption.isEnabled = false
        } else {
            mMeetingSecurityHeader.waitingRoomView.alpha = AppConstants.ALPHA_WAITINGROOM_ENABLED
            mMeetingSecurityHeader.waitingRoomView.swSecurityOption.isEnabled = true
        }
    }

    private fun respondToFrictionFreeStatus(enabled: Boolean) {
        mMeetingSecurityHeader.restrictSharingView.swSecurityOption.isChecked = !enabled
    }

    private fun respondToUserFlowStatusUpdate(userFlowStatus: UserFlowStatus) {
        when (userFlowStatus) {
            UserFlowStatus.FRICTION_FREE_ON_FAILURE -> respondToFrictionFreeStatus(false)
            UserFlowStatus.FRICTION_FREE_OFF_FAILURE -> respondToFrictionFreeStatus(true)
            UserFlowStatus.WAITING_ROOM_ON_FAILURE -> respondToWaitingRoomState(false)
            UserFlowStatus.WAITING_ROOM_OFF_FAILURE -> respondToWaitingRoomState(true)
        }
    }

    private fun respondToWaitingRoomState(enabled: Boolean) {
        mMeetingSecurityHeader.waitingRoomView.swSecurityOption.isChecked = enabled
        updateUIForRoomRole()
        mMeetingSecurityAdapter?.notifyDataSetChanged()
    }

    private fun respondToWaitingListUpdate(users: List<User>) {
        updateUIForRoomRole()
        updateGuestWaitingList(users)
    }

    private fun updateGuestWaitingList(users: List<User>) {
        mUserList.clear()
        mUserList.addAll(users)
        val layoutManager = recyclerView.layoutManager
        var lastVisiblePosition = 0
        if (layoutManager is LinearLayoutManager) {
            lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
        }
        mMeetingSecurityAdapter?.notifyDataSetChanged()
        recyclerView.scrollToPosition(lastVisiblePosition)
        mMeetingSecurityHeader.tvWaitingRoomGuestListCount.text = mUserList.size.toString()
        mUserList.let {
            mMeetingSecurityHeader.tvAdmitAll.visibility = if (it.size > 0) View.VISIBLE else View.GONE
        }
    }

    fun updateUIForRoomRole() {
        val isMeetingHost = mIsMeetingHost || isDelegateHost
        val isPresenter = mMeetingEventsModel?.isCurrentUserPresenter() ?: false

        mMeetingSecurityHeader.lockRoomView.visibility = if (isMeetingHost) View.VISIBLE else View.GONE

        val shouldShowWaitingRoomOption = isMeetingHost && !isWaitingRoomEnabledLocked
        mMeetingSecurityHeader.waitingRoomView.visibility = if (shouldShowWaitingRoomOption) View.VISIBLE else View.GONE

        val shouldShowRestrictSharingOption = isMeetingHost && !isFrictionFreeEnabledLocked
        mMeetingSecurityHeader.restrictSharingView.visibility = if (shouldShowRestrictSharingOption) View.VISIBLE else View.GONE

        val isWaitingRoomEnabled = mMeetingEventsModel?.waitingRoomEnabledStatus?.value ?: false
        val shouldShowWaitingRoomGuestSection = (isWaitingRoomEnabled && !isWaitingRoomEnabledLocked) || isPresenter
        mMeetingSecurityHeader.rlWaitingRoomGuestFragment.visibility = if (shouldShowWaitingRoomGuestSection) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isMeetingHost || isPresenter) View.VISIBLE else View.GONE
    }

    companion object {
        private val TAG = MeetingSecurityFragment::class.java.simpleName
        private const val MIXPANEL_EVENT_MESSAGE = "Mixpanel Event: Manage Meeting Security - "
        @JvmStatic
        fun newInstance(): MeetingSecurityFragment {
            return MeetingSecurityFragment()
        }
    }

    fun onUpdateInternetAvailability(isAvailable: Boolean) {
        mInternetReconnectingBannerLayout.visibility = if (isAvailable) View.GONE else View.VISIBLE
    }

    fun sendMixpanelWaitingRoomControlEvent(eventName: WaitingRoomEvents) {

        mLogger.mixpanelMeetingSecurityModel.waitingRoomAction = eventName.toString()
        mLogger.mixpanelMeetingSecurityModel.numGuestsAdmitted = numGuestsAdmitted
        mLogger.mixpanelMeetingSecurityModel.guestsAdmitted = guestAdmitted
        mLogger.mixpanelMeetingSecurityModel.numGuestsDenied = numGuestsDenied
        mLogger.mixpanelMeetingSecurityModel.guestsDenied = guestDenied

        val msg = MIXPANEL_EVENT_MESSAGE + eventName.toString()
        mLogger.info(MeetingSecurityFragment.TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_MANAGE_MEETING_SECURITY, msg ,null,
                      null, false, true)

        clearMixpanelValues()
    }

    private fun clearMixpanelValues() {
        numGuestsAdmitted = 0
        numGuestsDenied = 0
        guestAdmitted = ArrayList()
        guestDenied = ArrayList()
    }
}
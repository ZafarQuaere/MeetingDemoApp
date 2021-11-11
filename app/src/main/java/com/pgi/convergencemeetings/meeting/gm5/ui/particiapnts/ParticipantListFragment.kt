package com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.broadcastreceivers.ShareSheetBroadcastReceiverInOutMeeting
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioStatus
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioType
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.UserFlowStatus
import com.pgi.convergencemeetings.meeting.gm5.data.model.ActiveTalker
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.logging.Logger
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ParticipantListFragment : Fragment() {

	@BindView(R.id.rv_web_participants_list)
	lateinit var mParticipantRecyclerView: RecyclerView

    @BindView(R.id.img_security_icon)
    lateinit var imgSecurityIcon: ImageView

	@BindView(R.id.iv_host_no_audio)
	lateinit var imgHostNoAudio: ImageView

	@BindView(R.id.iv_host_mute_icon)
    lateinit var imgHostMuteIcon: ImageView

	@BindView(R.id.btn_security_menu)
	lateinit var btnSecurityMenu: View

    @BindView(R.id.tv_security_header_description)
    lateinit var tvSecurityHeaderDescription: TextView

	@BindView(R.id.tv_host_participant_name)
	lateinit var tvHostName : TextView

	@BindView(R.id.tv_host_room_type)
	lateinit var tvRoomType : TextView

	@BindView(R.id.tv_host_name_initials)
	lateinit var tvHostNameInitials : TextView

	@BindView(R.id.textGuestCount)
	lateinit var textGuestCount : TextView

	@BindView(R.id.ib_host_voice_signal)
	lateinit var hostSpeakerSignal : ImageButton

	@BindView(R.id.profile_pic_host_part_list)
	lateinit var ivHostProfile : CircleImageView

	@BindView(R.id.pb_host_audio_connecting)
	lateinit var audioConnectionProgress : ProgressBar

	var mWebParticipantListAdapter: WebParticipantListAdapter? = null
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)

	@BindView(R.id.rel_host_layout)
	lateinit var mHostRelativeLayout : RelativeLayout

	var mUserList: MutableList<User> = ArrayList()
	var mMeetingEventsModel: MeetingEventViewModel? = null
        set
	var mMeetingUserViewModel: MeetingUserViewModel? = null
        set

    private var isDelegateHost = false
    private var mIsMeetingHost = false
    public var hostAlreadyJoined = false

	private val mlogger: Logger = CoreApplication.mLogger
	private val TAG = ParticipantListFragment::class.java.simpleName

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	lateinit var mSelfUser: User
	var mHostList: MutableList<User> = ArrayList()
	
	@BindView(R.id.btn_share_sheet_menu)
	lateinit var btnShareSheetMenu: View
	var parentActivity : WebMeetingActivity? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		NewRelic.setInteractionName(Interactions.PARTICIPANT_LIST.interaction)
		mMeetingEventsModel = activity?.let { ViewModelProviders.of(it).get(MeetingEventViewModel::class.java) }
		mMeetingUserViewModel = activity?.let { ViewModelProviders.of(it).get(MeetingUserViewModel::class.java) }
		initializeParentActivity(activity)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.web_participants_list_view, container, false)
		ButterKnife.bind(this, view)
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val layoutManager = LinearLayoutManager(context)
		mParticipantRecyclerView.layoutManager = layoutManager
		registerViewModelListener()
		updateParticipantView()
		updateSecurityUI()
	}

	private fun clickHostHeader(userHost: User?) {
		mHostRelativeLayout.setOnClickListener {
			if (userHost != null) {
				mWebParticipantListAdapter?.showPopup(0, mHostList, mMeetingEventsModel?.getCurrentUser())
			}
		}
	}

	// update Host and Guest sections when any participant joins and left the meeting.
	public fun updateParticipantView() {
		mUserList = ParticipantsOrder.getSortedList(mUserList)
		val guestList = mutableListOf<User>()
		var hostUser : User? = null
		if (mUserList.isNotEmpty() && mUserList.size > 0 ){
			mUserList.forEachIndexed{index,element ->
				element.name = element.firstName +AppConstants.BLANK_SPACE+ element.lastName
				run {
					if (element.roomRole ==  AppConstants.HOST && !element.delegateRole && !hostAlreadyJoined) {
						hostUser = mUserList[index]
						hostAlreadyJoined = true
						mHostList.clear()
						mHostList.add(hostUser!!)
					} else {
						guestList.add(element)
					}
				}
			}
		}

		mWebParticipantListAdapter = mMeetingUserViewModel?.let { context?.let { it1 -> WebParticipantListAdapter(it, it1, guestList,false) } }
		mParticipantRecyclerView.adapter = mWebParticipantListAdapter
		textGuestCount.text = "${guestList.size}"
		updateHostUI(hostUser)
		updatedMenuListData(hostUser)
	}

	fun updatedMenuListData(userHost: User?) {
		if (mUserList.isNotEmpty() && mUserList.size > 0) {
			for (user in mUserList) {
				if (user.isSelf) {
					mSelfUser = user
					break
				}
			}
			clickHostHeader(userHost)
		}
	}

	// Update host section
	 fun updateHostUI(user: User?) {
		if (user != null) {
			val isMuted = user.audio.mute
			val audioConnected = user.audio.isConnected
			tvHostName.text = user.name
			tvRoomType.text = getString(R.string.host_lowercase)
			setAudioSpinnerVisibility(audioConnectionProgress,imgHostNoAudio, user)
			ivHostProfile.visibility = View.VISIBLE
			imgHostMuteIcon.visibility = if (isMuted) View.VISIBLE else View.GONE
			activity?.getColor(R.color.participant_name_color)?.let { tvHostName.setTextColor(it) }
			activity?.getColor(R.color.participant_type_color)?.let { tvRoomType.setTextColor(it) }
			imgHostNoAudio.visibility = if (!audioConnected && audioConnectionProgress.visibility == View.GONE) View.VISIBLE else View.GONE
			imgHostNoAudio.setBackgroundResource(R.drawable.audio_not_connected_icon)
			if (user.profileImage != null) {
				Picasso.get().load(user.profileImage).fit().into(ivHostProfile, object : Callback {
					override fun onSuccess() {
						tvHostNameInitials.visibility = View.INVISIBLE
						ivHostProfile.visibility = View.VISIBLE
					}
					override fun onError(e: Exception) {
						mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.PARTICIPANTLIST,
								"WebParticipantListFragment: error getting profile picture of host", e, null, true, false)
					}
				})
			} else {
				ivHostProfile.visibility = View.GONE
				tvHostNameInitials.text = user.initials
			}
		} else {
			updateDisabledHostUI()
		}
	}

	fun setAudioSpinnerVisibility(audioConnectionProgress: ProgressBar,noAudioIcon: ImageView, user: User) {
		val isSelf = user.isSelf
		if (mMeetingUserViewModel?.audioConnType == AudioType.DIAL_OUT) {
			audioConnectionProgress.visibility = if (user.audio.isConnecting) {
				noAudioIcon.visibility = View.GONE
				View.VISIBLE
			} else View.GONE
		} else {
			audioConnectionProgress.visibility = if (isSelf && user.audio.isConnecting && user.audio.isVoip) {
				noAudioIcon.visibility = View.GONE
				View.VISIBLE
			} else View.GONE
		}
		audioConnectionProgress.invalidate()
	}

	// Default disable host section, when host is not joined the meeting or host left the meeting
	 fun updateDisabledHostUI() {
		// Getting host information when host is not joined.
		val meetingRoomInfo = mMeetingUserViewModel?.getRoomInfoResponse()
		tvHostName.text = meetingRoomInfo?.meetingOwnerGivenName + AppConstants.BLANK_SPACE + meetingRoomInfo?.meetingOwnerFamilyName
		tvRoomType.text = activity?.getString(R.string.host_lowercase)
		ivHostProfile.visibility = View.GONE
		imgHostMuteIcon.visibility = View.GONE
		audioConnectionProgress.visibility = View.GONE
		tvHostNameInitials.visibility = View.VISIBLE
		imgHostNoAudio.visibility = View.VISIBLE
		imgHostNoAudio.setBackgroundResource(R.drawable.audio_not_connected_host_disable_icon)
		tvHostNameInitials.text = CommonUtils.getNameInitials(meetingRoomInfo?.meetingOwnerGivenName, meetingRoomInfo?.meetingOwnerFamilyName)
		activity?.getColor(R.color.participant_host_section_disable)?.let { tvHostName.setTextColor(it) }
		activity?.getColor(R.color.participant_host_section_disable)?.let { tvRoomType.setTextColor(it) }
	}

	private fun registerViewModelListener() {
		mMeetingEventsModel?.users?.observe(this, Observer { users: List<User> ->
			val layoutManager = mParticipantRecyclerView.layoutManager
			var lastVisiblePosition = 0
			if (layoutManager is LinearLayoutManager) {
				lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
			}
			mUserList.clear()
			mUserList.addAll(users)
			hostAlreadyJoined = false
			updateParticipantView()
			mWebParticipantListAdapter?.setHostUser(mUserList)
			mWebParticipantListAdapter?.notifyDataSetChanged()
			mParticipantRecyclerView.scrollToPosition(lastVisiblePosition)
			updateSecurityUI()
		})
        mMeetingEventsModel?.apply {
			activeTalker.observe(this@ParticipantListFragment, Observer {talker: ActiveTalker -> updateHostSpeaker(talker)})
			userFlowStatus.observe(this@ParticipantListFragment, Observer { userFlowStatus: UserFlowStatus -> respondToUserFlowStatus(userFlowStatus) })
			waitingRoomEnabledStatus.observe(this@ParticipantListFragment, Observer { updateSecurityUI() })
			this@ParticipantListFragment.isDelegateHost = getCurrentUser()?.delegateRole ?: false
			meetingLockStatus.observe(this@ParticipantListFragment, androidx.lifecycle.Observer { updateSecurityUI() })
			guestWaitingList.observe(this@ParticipantListFragment, androidx.lifecycle.Observer { users: List<User> -> updateGuestWaitingListCount(users?.size) })
			frictionFreeEnabledStatus.observe(this@ParticipantListFragment, Observer { updateSecurityUI() })
		}
        mMeetingUserViewModel?.apply {
			audioStatus.observe(this@ParticipantListFragment, Observer { status: AudioStatus -> setAudioProgressState(status) })
            userFlowStatus.observe(this@ParticipantListFragment, Observer { userFlowStatus: UserFlowStatus -> respondToUserFlowStatus(userFlowStatus) })
            waitingRoomEnabledStatus.observe(this@ParticipantListFragment, Observer { updateSecurityUI() })
			authResponse?.let {
				this@ParticipantListFragment.mIsMeetingHost = it.roomRole.toLowerCase() == AppConstants.HOST.toLowerCase()
			}
        }
	}

	// Update host speaker
	 fun updateHostSpeaker(talking: ActiveTalker) {
		if (talking.user.roomRole == AppConstants.HOST && !talking.user.delegateRole && talking.isTalking) {
			if (mHostList.size > 0 && mHostList[0].id == talking.user.id) {
				hostSpeakerSignal.visibility = View.VISIBLE
			} else {
				hostSpeakerSignal.visibility = View.INVISIBLE
				mWebParticipantListAdapter?.updateGuestTalker(talking)
			}
		} else {
			hostSpeakerSignal.visibility = View.INVISIBLE
			mWebParticipantListAdapter?.updateGuestTalker(talking)
			mWebParticipantListAdapter?.notifyDataSetChanged()
		}
}

	fun respondToUserFlowStatus(userFlowStatus: UserFlowStatus) {
        when (userFlowStatus) {
            UserFlowStatus.JOIN_MEETING_SUCCESS -> updateSecurityUI()
			UserFlowStatus.DISMISS_PARTICIPANT -> updateDisabledHostUI()
			UserFlowStatus.MUTE_UNMUTE -> if(mHostList.size>0)updateHostUI(mHostList[0])
		}
    }

    fun updateSecurityUI() {
		val isHost = mIsMeetingHost || isDelegateHost
		val isPresenter = mMeetingEventsModel?.isCurrentUserPresenter() ?: false
		val isWaitingRoomEnabled = mMeetingEventsModel?.waitingRoomEnabledStatus?.value ?: false

        val shouldShowSecurityOption = isHost || (isPresenter && isWaitingRoomEnabled)
        updateMeetingHeaderSecurityIconAndDescription(shouldShowSecurityOption)
	}

	private fun setAudioProgressState(status: AudioStatus) {
		if (status === AudioStatus.CONNECTING && mMeetingUserViewModel?.audioConnType === AudioType.VOIP) {
			mWebParticipantListAdapter?.setAudioConnectionAllowed(true)
		} else if (status === AudioStatus.CONNECTED) {
			mWebParticipantListAdapter?.setAudioConnectionAllowed(false)
		} else if (status === AudioStatus.DISCONNECTED || status === AudioStatus.NOT_CONNECTED) {
			mWebParticipantListAdapter?.setAudioConnectionAllowed(false)
		}
	}

	fun onServiceRetryFailed(retryStatus: RetryStatus) {
		(activity as BaseActivity).onServiceRetryFailed(retryStatus)
	}

	fun onUpdateInternetAvailability(isAvailable: Boolean) {
		mWebParticipantListAdapter?.onUpdateInternetAvailability(isAvailable)
		mWebParticipantListAdapter?.notifyDataSetChanged()
	}

	@OnClick(R.id.btn_security_menu)
	fun onSecurityIconClick() {
		(activity as? WebMeetingActivity)?.onSecurityMeetingOptionClicked()
	}

	private fun updateMeetingHeaderSecurityIconAndDescription(shouldShowSecurityOption: Boolean) {    //update Security icon and security header description on security option turned on or off

		if (!shouldShowSecurityOption) {  // this case handle when user role is guest or presenter
            btnSecurityMenu.visibility = View.GONE
			tvSecurityHeaderDescription.visibility = View.GONE
		} else {
			when {
				mMeetingEventsModel?.meetingLockStatus?.value == true -> {
					btnSecurityMenu.visibility = View.VISIBLE
					imgSecurityIcon.setImageResource(R.drawable.security_icon_green)
					tvSecurityHeaderDescription.visibility = View.VISIBLE
					tvSecurityHeaderDescription.text = getString(R.string.meeting_is_locked)
				}
				mMeetingEventsModel?.waitingRoomEnabledStatus?.value == true -> {
                    btnSecurityMenu.visibility = View.VISIBLE
					imgSecurityIcon.setImageResource(R.drawable.security_icon_green)
					tvSecurityHeaderDescription.visibility = View.VISIBLE
					updateGuestWaitingListCount(mMeetingEventsModel?.guestWaitingList?.value?.size ?: 0)
				}
                mMeetingEventsModel?.frictionFreeEnabledStatus?.value == false -> {
                    btnSecurityMenu.visibility = View.VISIBLE
					imgSecurityIcon.setImageResource(R.drawable.security_icon_green)
					tvSecurityHeaderDescription.visibility = View.GONE
				}
				else -> {
					imgSecurityIcon.setImageResource(R.drawable.security_icon_blue)
					tvSecurityHeaderDescription.visibility = View.GONE
				}
			}
		}
	}

	private fun updateGuestWaitingListCount(waitingGuestCount: Int) {
		//update Number of guest in waiting room on Guest tab on security menu waiting room on/off
		tvSecurityHeaderDescription.text = when (waitingGuestCount) {
			0 -> getString(R.string.host_waiting_room_no_guest)
			1 -> getString(R.string.one_person_in_waiting_room)
			else -> getString(R.string.host_waiting_room_guest_count_msg, waitingGuestCount)
		}

	}

	@OnClick(R.id.btn_share_sheet_menu)
	fun onShareSheetIconClick() {
		shareMeetingURL()
	}

	fun shareMeetingURL() {
		val sendIntent: Intent = Intent().apply {
			action = Intent.ACTION_SEND
			type = AppConstants.SHARE_TEXT_PLAIN
			val meetingUrl = String.format(parentActivity?.getString(R.string.please_join_the_video_meeting_at) + AppConstants.COLON + AppConstants.BLANK_SPACE + mMeetingUserViewModel?.meetingFurl)
			val dialInNumber = String.format(parentActivity?.getString(R.string.or_dial_in_with_this_number) + AppConstants.COLON) + AppConstants.BLANK_SPACE + mMeetingUserViewModel?.meetingRoomInfoResponse?.audio?.primaryAccessNumber
			val guestPasscode = String.format(parentActivity?.getString(R.string.guest_passcode) + AppConstants.COLON) + AppConstants.BLANK_SPACE + mMeetingUserViewModel?.meetingRoomInfoResponse?.audio?.guestPasscode
			val title = mMeetingUserViewModel?.meetingRoomInfoResponse?.title
			var shareMessage: String?
			shareMessage = if(!mMeetingUserViewModel?.meetingRoomInfoResponse?.audio?.primaryAccessNumber.isNullOrEmpty() && !mMeetingUserViewModel?.meetingRoomInfoResponse?.audio?.guestPasscode.isNullOrEmpty()) {
				meetingUrl + AppConstants.NEW_LINE_CHARACTER + AppConstants.NEW_LINE_CHARACTER + dialInNumber + AppConstants.NEW_LINE_CHARACTER + guestPasscode
			} else {
				meetingUrl
			}
			val subject = getString(R.string.invitation_to)?.let { String.format(it, AppConstants.BLANK_SPACE + title) }
			putExtra(Intent.EXTRA_SUBJECT, subject)
			putExtra(Intent.EXTRA_TITLE, title)
			putExtra(Intent.EXTRA_TEXT, shareMessage)
		}
		parentActivity?.let {
			val pi = PendingIntent.getBroadcast(parentActivity, AppConstants.ZERO,
					Intent(parentActivity, ShareSheetBroadcastReceiverInOutMeeting::class.java).putExtra(AppConstants.MEETING,AppConstants.IN_MEETING_SHARE),
					PendingIntent.FLAG_UPDATE_CURRENT)
			val shareIntent = Intent.createChooser(sendIntent, null, pi.intentSender)
			startActivity(shareIntent)
		}
	}

	fun initializeParentActivity(activity: FragmentActivity?) {
		activity?.let {
			if (it is WebMeetingActivity)
				parentActivity = it
		}
	}
}
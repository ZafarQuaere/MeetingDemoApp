package com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.VisibleForTesting
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.HostControlsEnum
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts.WebParticipantListAdapter.UserViewHolder
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.AudioType
import com.pgi.convergencemeetings.meeting.gm5.data.model.ActiveTalker
import com.pgi.convergencemeetings.models.HostControlsModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.logging.Logger
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class WebParticipantListAdapter(var mMeetingUserViewModel: MeetingUserViewModel,
								private val mContext: Context,
								private val mUsersList: List<User>,
								private val isPrivateChat: Boolean) : RecyclerView.Adapter<UserViewHolder>() {

	private val mlogger: Logger = CoreApplication.mLogger
	private var isAudioConnectionAllow = false
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mSelfUser: User? = null
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mBottomSheetPopupDialog: BottomSheetDialog? = null
	private var mIsInternetAvailable = true
	var istalking = false

	private val TAG = WebParticipantListAdapter::class.java.simpleName
	private var mIsMeetingHost = false
	var onShowChatDetailPage: ((User) -> Unit)? = null

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mIsPrivateChat = isPrivateChat

	fun setHostUser(usersList: MutableList<User>){
		for (user in usersList) {
			if (user.isSelf) {
				mSelfUser = user
				break
			}
		}
	}
	private val isLocaleGerman: Boolean = com.pgi.convergence.utils.CommonUtils.isUsersLocaleGerman()
	lateinit var  mActiveGuestTalker:ActiveTalker

	fun onUpdateInternetAvailability(isAvailable: Boolean) {
		mIsInternetAvailable = isAvailable
	}

	fun updateGuestTalker(activeTalker: ActiveTalker){
		mActiveGuestTalker = activeTalker
		istalking = true
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.web_participants_list_item,
				parent, false)
		val holder = UserViewHolder(view)
		mMeetingUserViewModel.authResponse.let { mIsMeetingHost = it?.roomRole?.toLowerCase() == AppConstants.HOST.toLowerCase() }

		return holder
	}

	override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
		val nameInitials = holder.mNameInitials
		val name = holder.mParticipantName
		val muteIcon = holder.mMuteIcon
		val profileImage = holder.mProfilePic
		val type = holder.mParticipantType
		val audioConnectingProgressBar = holder.mAudioConnectingProgressBar
		val noAudioIcon = holder.noAudioIcon
		val hostControlRow = holder.mHostControlRow
		if(!istalking){
			mBottomSheetPopupDialog?.dismiss()
		}
		val mImageGuestVoiceSignal = holder.imageGuestVoiceSignal
		if (mUsersList.isNotEmpty()) {
			val user = mUsersList[position]
			val audioConnected = user.audio.isConnected
			val isMuted = user.audio.mute
			setUserTypeSuperText(type, user)
			setUserInitialsOrPic(nameInitials, profileImage, user)
			name.text = user.name
			muteIcon.visibility = if (isMuted) View.VISIBLE else View.GONE
			noAudioIcon.visibility = if (!audioConnected && audioConnectingProgressBar.visibility == View.GONE) View.VISIBLE else View.GONE
			setAudioConnectionState(audioConnectingProgressBar,noAudioIcon, user)
			hostControlRow.isClickable = getHostControlsVisibility(user, audioConnectingProgressBar.visibility)
			hostControlRow.isEnabled = mIsInternetAvailable
			if(istalking){
				updateGusetSpeaker(mImageGuestVoiceSignal,user)
			}
		}
		if (mIsPrivateChat) {
			muteIcon.visibility = View.GONE
			noAudioIcon.visibility =  View.GONE
			hostControlRow.isClickable = true
		}
	}

	private fun updateGusetSpeaker(talking: ImageView, user: User) {
		if(mActiveGuestTalker.user.id == user.id  ){
			if (mActiveGuestTalker.let { it.isTalking}) {
				talking.visibility = View.VISIBLE
			} else {
				talking.visibility = View.INVISIBLE
			}
		}
	}

	fun setAudioConnectionState(progressBar: ProgressBar, noAudioIcon: ImageView, user: User) {
		val isSelf = user.isSelf
		if (mMeetingUserViewModel.audioConnType == AudioType.DIAL_OUT) {
			progressBar.visibility = if (user.audio.isConnecting) {
				noAudioIcon.visibility = View.GONE
				View.VISIBLE
			} else
				View.GONE
		} else {
			progressBar.visibility = if (isSelf && isAudioConnectionAllow) {
				noAudioIcon.visibility = View.GONE
				View.VISIBLE
			} else
				View.GONE
		}
		progressBar.invalidate()
	}

	private fun setUserTypeSuperText(type: TextView?, user: User) {
		val roomRole = user.roomRole
		val host = mContext.resources.getString(R.string.host_lowercase)
		val coHost = mContext.resources.getString(R.string.roleDelegateHost_lowercase)
		val presenter = mContext.resources.getString(R.string.presenter_lowercase)
		val isPresenter = roomRole == AppConstants.PRESENTER || user.promoted
		val isCoHost = user.delegateRole
		val isHost = roomRole == AppConstants.HOST
		val nonHostType = if (isPresenter) presenter else mContext.resources.getString(R.string.guest_lowercase)
		val userTypeText = if (isHost) if (isCoHost) coHost else host else nonHostType
		val userTypeVisible = if (roomRole == "GUEST" && !user.promoted) View.GONE else View.VISIBLE
		type?.text = userTypeText
		type?.visibility = if (mIsPrivateChat) View.VISIBLE else userTypeVisible
	}

	 fun setUserInitialsOrPic(nameInitials: TextView?, profileImage: CircleImageView?, user: User) {
		val profilePic = user.profileImage
		nameInitials?.text = user.initials
		nameInitials?.visibility = View.VISIBLE
		profileImage?.visibility = View.INVISIBLE
		nameInitials?.setBackgroundResource(R.drawable.recent_meetings_circle_drawable)
		if (profilePic != null) {
			Picasso.get().load(profilePic).fit().into(profileImage, object : Callback {
				override fun onSuccess() {
					profileImage?.visibility = View.VISIBLE
					nameInitials?.visibility = View.INVISIBLE
				}
				override fun onError(e: Exception) {
					mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.PARTICIPANTLIST,
							"WebParticipantListAdapter: error getting profile picture for participant", e, null, true, false)
				}
			})
		} else if (user.initials.equals(AppConstants.POUND_SYMBOL)) {
			nameInitials?.setBackgroundResource(R.drawable.avatar59)
			nameInitials?.text = ""
		} else {
			nameInitials?.setBackgroundResource(R.drawable.recent_meetings_circle_drawable)
			nameInitials?.text = user.initials
		}
	 }

	override fun getItemId(position: Int): Long {
		val user = mUsersList[position]
		return user.id?.toLong() ?: 0
	}

	override fun getItemCount(): Int {
		return mUsersList.size
	}

	fun getHostControlsVisibility(user: User, audioProgressVisibility: Int): Boolean {
		var showMenuIcon = false
		if (audioProgressVisibility != View.VISIBLE) {
			showMenuIcon = user.id != mSelfUser?.id
		}
		return showMenuIcon
	}

	fun setAudioConnectionAllowed(isAllowed: Boolean) {
		isAudioConnectionAllow = isAllowed
	}

	fun showPopup(position: Int, mUserList: List<User>, currentUser: User?) {
		mBottomSheetPopupDialog = BottomSheetDialog(mContext)
		val viewLayout =  LayoutInflater.from(mContext).inflate(R.layout.bottom_menu_list_host_control,null)
		val listView: ListView = viewLayout.findViewById(R.id.list_menu_control)
		mBottomSheetPopupDialog?.setContentView(viewLayout)
		val user = mUserList[position]
		val menuList = if (currentUser?.roomRole == AppConstants.GUEST) {
			getMenuListForGuest(user)
		} else {
			getMenuList(user) as MutableList<HostControlsModel>
		}
		if (menuList.isNotEmpty()) {
			val adapter = HostMenuControlsAdapter(mBottomSheetPopupDialog!!, user, mMeetingUserViewModel,
					mContext, R.layout.menu_item_host_control, menuList)
			listView.adapter = adapter
			mBottomSheetPopupDialog?.show()
		}
	}

	fun getMenuListForGuest(user: User): MutableList<HostControlsModel> {
		val guestControls: MutableList<HostControlsModel> = ArrayList()
		if (user.isSelf || user.firstName?.isDigitsOnly() == true || user.audio.id == user.id) {
			return guestControls
		} else {
			val privateChatModel = HostControlsModel()
			privateChatModel.controlName = AppConstants.MENU_PRIVATE_CHAT_WITH
			privateChatModel.hostControlType = HostControlsEnum.PRIVATE_CHAT_WITH
			guestControls.add(privateChatModel)
		}
		return guestControls
	}

	 fun getMenuList(user: User): List<HostControlsModel> {
		val hostControls: MutableList<HostControlsModel> = ArrayList()
		try {
			if (!mMeetingUserViewModel?.isPrivateChatLocked) {
			//start a private chat from the guests tab
				if (!user.isSelf && !user.initials.equals(AppConstants.POUND_SYMBOL) && user.audio.id != user.id) {
					val privateChatModel = HostControlsModel()
					privateChatModel.controlName = AppConstants.MENU_PRIVATE_CHAT_WITH
					privateChatModel.hostControlType = HostControlsEnum.PRIVATE_CHAT_WITH
					hostControls.add(privateChatModel)
				}
			}
			if (user.audio.isConnected) {
				hostControls.add(getAudioUserControls(user))
			}
			if (mSelfUser?.hasControls!! && !mSelfUser?.promoted!!) {
				val dismissModel = HostControlsModel()
				dismissModel.controlName = AppConstants.MENU_DISMISS
				//Adding promote/demote control for host with both audio and web support
				if (user.id != user.audio.id) {
					val promoteModel = HostControlsModel()
					if (user.roomRole != "HOST") {
						if (user.promoted) {
							promoteModel.controlName = AppConstants.MENU_DEMOTE
							promoteModel.hostControlType = HostControlsEnum.DEMOTE
						} else {
							promoteModel.controlName = AppConstants.MENU_PROMOTE
							promoteModel.hostControlType = HostControlsEnum.PROMOTE
						}
						hostControls.add(promoteModel)
					}
					// Adding dismiss participant control
					dismissModel.hostControlType = HostControlsEnum.DISMISS
					hostControls.add(dismissModel)
				} else {
					// Adding dismiss Audio only participant control
					dismissModel.hostControlType = HostControlsEnum.DISMISS_AUDIO_PARTICIPANT
					hostControls.add(dismissModel)
				}
			}
		} catch (ex: Exception) {
			mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.PARTICIPANTLIST,
					"WebParticipantListAdapter: error getting menu list for participant", ex, null, true, false)
		}
		return hostControls
	}

	 fun getAudioUserControls(user: User): HostControlsModel {
		val muteModel = HostControlsModel()
		if (user.audio.mute) {
			muteModel.controlName = AppConstants.MENU_UNMUTE
			muteModel.hostControlType = HostControlsEnum.UNMUTE
		} else {
			muteModel.controlName = AppConstants.MENU_MUTE
			muteModel.hostControlType = HostControlsEnum.MUTE
		}
		return muteModel
	}

	inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		@BindView(R.id.tv_gm5_name_initials)
		lateinit var mNameInitials: TextView

		@BindView(R.id.tv_gm5_participant_type)
		lateinit var mParticipantType: TextView

		@BindView(R.id.tv_gm5_participant_name)
		lateinit var mParticipantName: TextView

		@BindView(R.id.iv_gm5_mute_icon)
		lateinit var mMuteIcon: ImageView

		@BindView(R.id.iv_gm5_no_audio)
		lateinit var noAudioIcon: ImageView

		@BindView(R.id.ib_guest_voice_signal)
		lateinit var imageGuestVoiceSignal: ImageView

		@BindView(R.id.pb_audio_connecting)
		lateinit var mAudioConnectingProgressBar: ProgressBar

		@BindView(R.id.profile_pic_part_list)
		lateinit var mProfilePic: CircleImageView

		@BindView(R.id.rel_gm5_host_control_row)
		lateinit var mHostControlRow: RelativeLayout

		@OnClick(R.id.rel_gm5_host_control_row)
		fun onHostControlClicked() {
			if (mIsPrivateChat) {
				mMeetingUserViewModel.isOpenChatFragment.postValue(true)
				onShowChatDetailPage?.invoke(mUsersList[adapterPosition])
			} else {
				showPopup(adapterPosition, mUsersList, mSelfUser)
			}
		}

		init {
			ButterKnife.bind(this, itemView)
		}
	}
}
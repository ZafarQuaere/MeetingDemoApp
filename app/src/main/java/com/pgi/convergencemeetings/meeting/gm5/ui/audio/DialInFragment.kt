package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.models.MeetingRoom
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.convergencemeetings.utils.ClientInfoDataSet
import com.pgi.convergencemeetings.utils.ClientInfoResultCache
import com.pgi.logging.enums.Interactions

class DialInFragment() : Fragment() {
     var mIsMeetingHost: Boolean = false
     lateinit var mDialInCallback: DialInFragmentContractor.DialInActivityContract
     var mIsUseHtml5: Boolean = false
     var name: String = ""
     var mobile: String = ""

    constructor(isMeetingHost: Boolean, dialInCallback: DialInFragmentContractor.DialInActivityContract, isUseHtml5: Boolean, Name: String, Mobile: String) : this() {
        mIsMeetingHost = isMeetingHost
        mDialInCallback = dialInCallback
        mIsUseHtml5 = isUseHtml5
        name = Name
        mobile = Mobile
    }

    @BindView(R.id.tv_donot_connect_audio)
    lateinit var btnDoNotConnectAudio: TextView

    @BindView(R.id.tv_dial_in_header)
    lateinit var tvMeetingName: TextView

    @BindView(R.id.primary_access_number)
    lateinit var primary_access_number: TextView

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var mSelectedPhoneNumber: String? = null

    var mClientInfoDataSet: ClientInfoDataSet? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var mAudioSelectionCallbacks: AudioSelectionFragmentContractor.activity? = null
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    lateinit var mMeetingUserViewModel: MeetingUserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NewRelic.setInteractionName(Interactions.DIALIN.interaction)
        mClientInfoDataSet = if (mIsMeetingHost) {
            ClientInfoDaoUtils.getInstance()
        } else {
            ClientInfoResultCache.getInstance()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dial_in_number_view, container, false)
        ButterKnife.bind(this, view)
        initAudioSelectionFragment()
        updateUI()
        return view
    }

     private fun initAudioSelectionFragment() {
         activity?.let {
             if(it is AudioSelectionFragmentContractor.activity){
                 mAudioSelectionCallbacks = it
             }
         }
    }

     fun updateUI() {
        val res = resources
        tvMeetingName.text = CommonUtils.formatCamelCase(name) + "'s" + " " + res.getString(R.string.meeting)
         if (mIsUseHtml5) {
             mMeetingUserViewModel = activity?.let { ViewModelProviders.of(it).get(MeetingUserViewModel::class.java) }!!
             val meetingRoomInfoResponse = mMeetingUserViewModel.getRoomInfoResponse()
             mSelectedPhoneNumber =  meetingRoomInfoResponse?.audio?.primaryAccessNumber
         } else {
             mSelectedPhoneNumber = try {
                 val meetingRoom: MeetingRoom = mClientInfoDataSet!!.meetingRoomData
                 meetingRoom.primaryAccessNumber
             } catch (e: Exception) {
                 ""
             }
         }
        if (mobile.isNotEmpty()) {
            primary_access_number.text = mobile
            mSelectedPhoneNumber = mobile
        } else {
            primary_access_number.text = mSelectedPhoneNumber
        }
        //Enable disable Connect audio connection option
        if (mIsUseHtml5) {
            btnDoNotConnectAudio.visibility = View.VISIBLE
        } else {
            btnDoNotConnectAudio.visibility = View.GONE
        }
    }

    @OnClick(R.id.iv_dial_in_back)
    fun onBackButtonClicked() {
        mDialInCallback.showAudioFragment()
    }

    @OnClick(R.id.rl_dialin_container)
    fun moveToDialInListClicked() {
        mAudioSelectionCallbacks?.handleDialInAction()
    }

    @OnClick(R.id.btn_dial_in)
    fun onDialInButtonClicked() {
        mSelectedPhoneNumber?.let { mDialInCallback.handleDialInClick(it) }
    }

    @OnClick(R.id.tv_donot_connect_audio)
    fun onDoNotConnectAudioClicked() {
        mAudioSelectionCallbacks?.handleDoNotConnectAudio()
    }
}
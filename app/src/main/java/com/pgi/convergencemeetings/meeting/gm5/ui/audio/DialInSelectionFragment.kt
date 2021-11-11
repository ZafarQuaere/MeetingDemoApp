package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.utils.DialInNumbers
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.convergencemeetings.utils.ClientInfoDataSet
import com.pgi.convergencemeetings.utils.ClientInfoResultCache
import com.pgi.logging.enums.Interactions
import com.pgi.network.models.AccessNumber
import java.util.*

/**
 * Created by ashwanikumar on 11/13/2017.
 */
class DialInSelectionFragment() : Fragment(), DialInNumberListener {

	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	var mClientInfoDataSet: ClientInfoDataSet? = null
	var phoneNumbersList: MutableList<DialInNumbers> = ArrayList()
	var mAudioSelectionCallbacks: AudioSelectionFragmentContractor.activity? = null
	var mIsMeetingHost: Boolean = false
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	lateinit var mMeetingUserViewModel: MeetingUserViewModel
	var mDialInCallback: DialInFragmentContractor.DialInActivityContract? = null
	var mIsUseHtml5: Boolean = false
	lateinit var  accessNumbers: List<AccessNumber>
	lateinit var descriptionLocation:String
	var primaryaccessNo:String? = ""

	constructor(isMeetingHost: Boolean, dialInCallbacknew: DialInFragmentContractor.DialInActivityContract, isUseHtml5new: Boolean) : this() {
		mIsMeetingHost = isMeetingHost
		mDialInCallback = dialInCallbacknew
		mIsUseHtml5 = isUseHtml5new
	}

	@BindView(R.id.iv_back_btn_dial_in)
	lateinit var ivBackButton: ImageView

	@BindView(R.id.rv_dialin_numbers)
	lateinit var rvDialInNumbers: RecyclerView

	@BindView(R.id.rl_dialin_container)
	lateinit var rlDialInDataContainer: RelativeLayout

	@BindView(R.id.tv_meeting_name_header)
	lateinit var tvMeetingNameHeader: TextView

	@BindView(R.id.tv_no_dialin_data)
	lateinit var tvNoDialInData: TextView

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		NewRelic.setInteractionName(Interactions.DIALIN_SEARCH.interaction)
		mClientInfoDataSet = if (mIsMeetingHost) {
			ClientInfoDaoUtils.getInstance()
		} else {
			ClientInfoResultCache.getInstance()
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.dial_in_number_selection_view, container, false)
		ButterKnife.bind(this, view)
		initAudioSelectionFragment()
		tvMeetingNameHeader.text= resources.getString(R.string.dial_in_number)
		mMeetingUserViewModel = activity?.let { ViewModelProviders.of(it).get(MeetingUserViewModel::class.java) }!!
		accessNumbers = mMeetingUserViewModel.getDialInNumbers()
		val meetingRoomInfoResponse = mMeetingUserViewModel.getRoomInfoResponse()
		primaryaccessNo =  meetingRoomInfoResponse?.audio?.primaryAccessNumber
		setupPhoneNumberList()
		return view
	}

	fun  setupPhoneNumberList() {
		phoneNumbersList.clear()
		if (mIsUseHtml5) {
			if (accessNumbers.isNotEmpty()) {
				for ((phoneNumber, phoneType, description) in accessNumbers) {
					val dialInNumbers: DialInNumbers = object : DialInNumbers {
						override fun getPhoneNumber(): String {
							return phoneNumber
						}

						override fun getLocation(): String {
							if(primaryaccessNo.equals(phoneNumber)){
								descriptionLocation = resources.getString(R.string.primary_access_number)
							} else {
								descriptionLocation = description
							}
							return descriptionLocation
						}

						override fun getPhoneType(): String {
							return phoneType
						}
					}
					phoneNumbersList.add(dialInNumbers)
				}
			}
		} else {
			val phoneInformationList = mClientInfoDataSet?.phoneNumbersList
			if (phoneInformationList != null && phoneInformationList.isNotEmpty()) {
				for (phoneInformation in phoneInformationList) {
					val dialInNumbers: DialInNumbers = phoneInformation
					phoneNumbersList.add(dialInNumbers)
				}
			}
		}
		setListIfEmpity()
	}

	fun setListIfEmpity(){
		if (phoneNumbersList.isNotEmpty()) {
			val linearLayoutManager = LinearLayoutManager(view?.context)
			rvDialInNumbers.layoutManager = linearLayoutManager
			val dialInNumberAdapter = context?.let { DialInNumberAdapter(it,phoneNumbersList, this) }
			rvDialInNumbers.adapter = dialInNumberAdapter
			rvDialInNumbers.setItemViewCacheSize(phoneNumbersList.size)
		} else {
			displayEmptyView()
		}
	}

	private fun initAudioSelectionFragment() {
		activity?.let {
			if(it is AudioSelectionFragmentContractor.activity){
				mAudioSelectionCallbacks = it
			}
		}
	}

	private  fun moveToDialInSearch(){
		mAudioSelectionCallbacks?.handledialinsearch()
	}

	fun displayEmptyView() {
		rlDialInDataContainer.visibility = View.GONE
		tvNoDialInData.visibility = View.VISIBLE
	}

	override fun onPhoneNumberSelected(phoneNumber: String) {
		mAudioSelectionCallbacks?.handleDialIn(phoneNumber)
	}

	@OnClick(R.id.iv_back_btn_dial_in)
	fun onBackButtonClicked() {
        activity?.supportFragmentManager?.popBackStack()
	}

	@OnClick(R.id.btn_search)
	fun onSearchClicked() {
		moveToDialInSearch()
	}
}
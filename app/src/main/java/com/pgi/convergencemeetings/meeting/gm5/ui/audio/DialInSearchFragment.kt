package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.DialInNumbers
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.convergencemeetings.utils.ClientInfoDataSet
import com.pgi.convergencemeetings.utils.ClientInfoResultCache
import com.pgi.logging.enums.Interactions
import com.pgi.network.models.AccessNumber
import kotlinx.android.synthetic.main.dial_in_search_activity.*
import java.util.*

class DialInSearchFragment () : Fragment(), DialInNumberListener {
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    var mClientInfoDataSet: ClientInfoDataSet? = null
    var dialInNumberAdapter: DialInNumberAdapter? = null
    var mphoneNumbersList: MutableList<DialInNumbers> = ArrayList()
    var mAudioSelectionCallbacks: AudioSelectionFragmentContractor.activity? = null
    var mIsMeetingHost: Boolean = false
    var mDialInCallback: DialInFragmentContractor.DialInActivityContract? = null
    var mIsUseHtml5: Boolean = false

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    lateinit var meetingUserViewModel: MeetingUserViewModel

    constructor(isMeetingHost: Boolean, dialInCallback: DialInFragmentContractor.DialInActivityContract, isUseHtml5: Boolean) : this() {
        mIsMeetingHost = isMeetingHost
        mDialInCallback = dialInCallback
        mIsUseHtml5 = isUseHtml5
    }

    @BindView(R.id.rv_dialin_numbers)
    lateinit var rvDialInNumbers: RecyclerView

    @BindView(R.id.searchEditText)
    lateinit var searchEditText: EditText

    @BindView(R.id.rl_dial_in_serach_container)
    lateinit var rlDialInDataContainer: RelativeLayout

    @BindView(R.id.tv_no_dial_in_data)
    lateinit var tvNoDialInData: TextView

    lateinit var  maccessNumbers: List<AccessNumber>
    lateinit var mdescriptionLocation:String
    var mprimaryaccessNo:String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NewRelic.setInteractionName(Interactions.DIALIN_SELECTION.interaction)
        mClientInfoDataSet = if (mIsMeetingHost) ClientInfoDaoUtils.getInstance() else ClientInfoResultCache.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dial_in_search_activity, container, false)
        ButterKnife.bind(this, view)
        initAudioSelectionFragment()
        meetingUserViewModel = activity?.let { ViewModelProviders.of(it).get(MeetingUserViewModel::class.java) }!!
        maccessNumbers = meetingUserViewModel.getDialInNumbers()
        val meetingRoomInfoResponse = meetingUserViewModel.getRoomInfoResponse()
        mprimaryaccessNo =  meetingRoomInfoResponse?.audio?.primaryAccessNumber
        setUpNumberList()
        setUpSearch()
        return view
    }

    private fun initAudioSelectionFragment() {
        activity?.let {
            if(it is AudioSelectionFragmentContractor.activity){
                mAudioSelectionCallbacks = it
            }
        }
    }

    fun  setUpNumberList() {
        mphoneNumbersList.clear()
        if (mIsUseHtml5) {
            if (maccessNumbers.isNotEmpty()) {
                for ((mphoneNumber, mphoneType, mdescription) in maccessNumbers) {
                    val dialInNumbers: DialInNumbers = object : DialInNumbers {
                        override fun getPhoneNumber(): String {
                            return mphoneNumber
                        }

                        override fun getLocation(): String {
                            if(mprimaryaccessNo.equals(mphoneNumber)){
                                mdescriptionLocation = resources.getString(R.string.primary_access_number)
                            } else {
                                mdescriptionLocation = mdescription
                            }
                            return mdescriptionLocation
                        }

                        override fun getPhoneType(): String {
                            return mphoneType
                        }
                    }
                    mphoneNumbersList.add(dialInNumbers)
                }
            }
        } else {
            val phoneInformationList = mClientInfoDataSet?.phoneNumbersList
            if (phoneInformationList != null && !phoneInformationList.isEmpty()) {
                for (phoneInformation in phoneInformationList) {
                    val dialInNumbers: DialInNumbers = phoneInformation
                    mphoneNumbersList.add(dialInNumbers)
                }
            }
        }
        setListIfEmpty()
    }


    fun setListIfEmpty(){
        if (mphoneNumbersList.isNotEmpty()) {
            val linearLayoutManager = LinearLayoutManager(view?.context)
            rvDialInNumbers.layoutManager = linearLayoutManager
             dialInNumberAdapter = context?.let { DialInNumberAdapter(it,mphoneNumbersList, this) }
            rvDialInNumbers.adapter = dialInNumberAdapter
            rvDialInNumbers.setItemViewCacheSize(mphoneNumbersList.size)
        } else {
            displayEmptyView()
        }
    }

    private fun setUpSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
             //afterTextChanged
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //beforeTextChanged
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    search_close_btn.visibility = View.VISIBLE
                    filter(s.toString())
                } else if (s.isEmpty()) {
                    search_close_btn.visibility = View.GONE
                } else {
                    search_close_btn.visibility = View.VISIBLE
                    setUpNumberList()
                }
            }
        })
    }

     fun filter(text: String) {
        val phoneNumbersfliterdList: MutableList<DialInNumbers> = ArrayList()
        for (item in mphoneNumbersList) {
            if (item.phoneNumber.toString().replace(" ","").contains(text)) {
                phoneNumbersfliterdList.add(item)
            } else if (item.location.toLowerCase().contains(text.toLowerCase())) {
                phoneNumbersfliterdList.add(item)
            }
        }

        dialInNumberAdapter?.addSerachList(phoneNumbersfliterdList)
        if(phoneNumbersfliterdList.isEmpty()) {
            displayEmptyView()
        } else {
//            Remove the empty view and show the filtered list
            rlDialInDataContainer.visibility = View.VISIBLE
            tvNoDialInData.visibility = View.GONE
        }
    }

    private fun displayEmptyView() {
        rlDialInDataContainer.visibility = View.GONE
        tvNoDialInData.visibility = View.VISIBLE
    }

    @OnClick(R.id.search_back_btn)
    fun onBackButtonClicked() {
        CommonUtils.hideKeyboard(activity)
        activity?.supportFragmentManager?.popBackStack()
    }

    @OnClick(R.id.search_close_btn)
    fun onSearchClose() {
        search_close_btn.visibility = View.GONE
        searchEditText.text.clear()
        CommonUtils.hideKeyboard(activity)
        mphoneNumbersList.clear()
        setUpNumberList()
        dialInNumberAdapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        CommonUtils.hideKeyboard(activity)
    }

    override fun onPhoneNumberSelected(phoneNumber: String) {
        mAudioSelectionCallbacks?.handleDialIn(phoneNumber)
    }

}
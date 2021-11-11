package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import com.pgi.convergencemeetings.utils.DialOutNumberListener
import com.pgi.logging.enums.Interactions
import java.util.*

/**
 * Created by ashwanikumar on 11/13/2017.
 */
class DialOutFragment : Fragment(), DialOutNumberListener {

	@BindView(R.id.iv_back_btn_dial_out)
	lateinit var ivBackButton: ImageView

	@BindView(R.id.tv_selected_phone_number)
	lateinit var tvSelectedPhoneNumber: Button

	@BindView(R.id.tv_dial_out_header_title)
	lateinit var tvDialOutHeaderTitle: TextView

	@BindView(R.id.btn_dial_out)
	lateinit var btnDialOut: Button

	@BindView(R.id.btn_donot_connect)
	lateinit var btnDontConnectAudio: Button

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mDialOutCallback: DialOutFragmentContrator.activity? = null
	private var mIsMeetingHost = false

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mSelectedPhoneNumber: Phone? = null

	private var mPhoneNumberList: ArrayList<Phone> = ArrayList()
	private var mContext: Context? = null
	private var mIsUseHtml5 = false

	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	lateinit var mMeetingHostName : String
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	 var mAudioSelectionCallbacks: AudioSelectionFragmentContractor.activity? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		NewRelic.setInteractionName(Interactions.DIAL_OUT.interaction)
	}

	fun initDialoutFragment(context: Context?, isMeetingHost: Boolean, mHostName: String,
													phoneList: List<Phone>, selectedPhoneNumber: Phone?, callback: DialOutFragmentContrator.activity?,
													isUseHtlm5: Boolean) {
		mContext = context
		mIsMeetingHost = isMeetingHost
		mMeetingHostName = mHostName
		mDialOutCallback = callback
		mIsUseHtml5 = isUseHtlm5
		mPhoneNumberList.addAll(phoneList)
		mSelectedPhoneNumber = selectedPhoneNumber
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.dial_out_view, container, false)
		ButterKnife.bind(this, view)
		val context = view.context
		initData()
		return view
	}

	fun initData()
	{
		tvDialOutHeaderTitle.text = mMeetingHostName.plus("'s ").plus(getString(R.string.meeting).toLowerCase())
		mSelectedPhoneNumber?.let { phoneNo ->
			tvSelectedPhoneNumber.text = if (!phoneNo.countryCode!!.startsWith(AppConstants.PLUS_SYMBOL))
				AppConstants.PLUS_SYMBOL + mSelectedPhoneNumber?.countryCode + AppConstants.BLANK_SPACE + mSelectedPhoneNumber?.number
			else
				phoneNo.countryCode + " " + mSelectedPhoneNumber?.number
		}

		activity?.let{
			if(it is AudioSelectionFragmentContractor.activity)
				mAudioSelectionCallbacks = it
		}

	}

	override fun onPhoneNumberSelected(phone: Phone) {
		mSelectedPhoneNumber = phone
	}

	@OnClick(R.id.iv_back_btn_dial_out)
	fun onBackButtonClicked() {
		mDialOutCallback?.showAudioFragment()
	}

	@OnClick(R.id.btn_dial_out)
	fun onDialOutButtonClicked() {
		 if (mSelectedPhoneNumber != null) {
				mDialOutCallback?.handleCallMeClick(mSelectedPhoneNumber!!)
		} else {
			Toast.makeText(mContext, resources.getString(R.string.please_select_phone_number), Toast.LENGTH_LONG).show()
		}
	}

	@OnClick(R.id.tv_selected_phone_number)
	fun onSelectedPhoneNumberClicked() {
		mDialOutCallback?.showDialOutSelectionFragment()
	}

	@OnClick(R.id.btn_donot_connect)
	fun onDoNotConnectAudioClicked() {
		mAudioSelectionCallbacks?.handleDoNotConnectAudio()
	}

	companion object {
		private val TAG = DialOutFragment::class.java.simpleName

		fun newInstance(): DialOutFragment {
			return DialOutFragment()
		}
	}
}
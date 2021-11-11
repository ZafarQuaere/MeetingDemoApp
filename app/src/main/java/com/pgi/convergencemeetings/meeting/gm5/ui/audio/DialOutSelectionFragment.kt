package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.gm5.ui.audio.AddNumberDialogFragment.AddNumberDialogListener
import com.pgi.convergencemeetings.meeting.BaseMeetingActivity
import com.pgi.convergencemeetings.models.getPhoneNumberModel.Phone
import com.pgi.convergencemeetings.utils.DialOutNumberListener
import com.pgi.logging.Logger
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import java.util.*

/**
 * Created by ashwanikumar on 11/13/2017.
 */
class DialOutSelectionFragment : Fragment(), DialOutNumberListener, AddNumberDialogListener, AudioSelectionFragmentContractor.fragment {

	private val logger: Logger = CoreApplication.mLogger

	@BindView(R.id.iv_back_btn_dial_out)
	lateinit var ivBackButton: ImageView

	@BindView(R.id.rv_dialout_numbers)
	lateinit var rvDialOutNumbers: RecyclerView

	@BindView(R.id.btn_dial_out)
	lateinit var btnDialOut: Button

	@BindView(R.id.btn_add_number)
	lateinit var rlBtnAddNumber: RelativeLayout

	var mDialOutCallback: DialOutFragmentContrator.activity? = null
	var mIsMeetingHost = false
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var  addPhoneNumber = false
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mSelectedPhoneNumber: Phone? = null
	private var mLinearLayoutManager: LinearLayoutManager? = null
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mPhoneNumberList: ArrayList<Phone> = ArrayList()
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mDialInNumberAdapter: DialOutNumberAdapter? = null
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mContext: Context? = null
	var mIsUseHtml5 = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		NewRelic.setInteractionName(Interactions.DIAL_OUT.interaction)
	}

	fun initDialoutFragment(context: Context?, isMeetingHost: Boolean, phoneList: List<Phone>, callback: DialOutFragmentContrator.activity?, isUseHtlm5: Boolean) {
		mContext = context
		mIsMeetingHost = isMeetingHost
		mDialOutCallback = callback
		mIsUseHtml5 = isUseHtlm5
		mPhoneNumberList.addAll(phoneList)
		if(mPhoneNumberList.isNotEmpty()) mSelectedPhoneNumber = mPhoneNumberList[0]
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.dial_out_number_selection_view, container, false)
		ButterKnife.bind(this, view)
		val context = view.context
		mLinearLayoutManager = LinearLayoutManager(context)
		rvDialOutNumbers.layoutManager = mLinearLayoutManager
		setListIfEmpty()
		return view
	}

	fun setListIfEmpty(){
		if (mPhoneNumberList.isEmpty()) {
			showAddNumberDialog()
			btnDialOut.visibility = View.INVISIBLE
		} else {
			initializeAdapter()
		}
	}

	private fun initializeAdapter() {
		mDialInNumberAdapter = DialOutNumberAdapter(mPhoneNumberList, this)
		rvDialOutNumbers.adapter = mDialInNumberAdapter
	}

	override fun onPhoneNumberSelected(phone: Phone?) {
		mSelectedPhoneNumber = phone
		mSelectedPhoneNumber?.let { mDialOutCallback?.showDialOutFragment(it) }
	}

	@OnClick(R.id.iv_back_btn_dial_out)
	fun onBackButtonClicked() {
		if(mSelectedPhoneNumber!=null)
			mDialOutCallback?.showDialOutFragment(mSelectedPhoneNumber!!)
		else
			mDialOutCallback?.showAudioFragment()
	}

	@OnClick(R.id.btn_dial_out)
	fun onDialOutButtonClicked() {
		if (mPhoneNumberList.isEmpty()) {
			mContext.let {
				Toast.makeText(it, resources.getString(R.string.please_add_phone_number), Toast.LENGTH_LONG).show()
			}
			showAddNumberDialog()
		} else if (mSelectedPhoneNumber != null) {
				mDialOutCallback?.handleCallMeClick(mSelectedPhoneNumber)
		} else {
			mContext.let {
				Toast.makeText(it, resources.getString(R.string.please_select_phone_number), Toast.LENGTH_LONG).show()
			}

		}
	}

	@OnClick(R.id.btn_add_number)
	fun onAddNumberClicked() {
		showAddNumberDialog()
	}

	 fun showAddNumberDialog() {
		val inputNameDialog = AddNumberDialogFragment()
		activity.let {
			if(it is BaseMeetingActivity){
				inputNameDialog.initAddNumberDialog((it), this)
				inputNameDialog.isCancelable = false
			}
		}

		fragmentManager?.let { inputNameDialog.show(it, "Add Number Dialog") }
	}

	override fun onNumberAdded(countryCode: String, phoneNumber: String) {
		logger.info(TAG, LogEvent.APP_VIEW, LogEventValue.DIALOUTSCREEN,
				"DialOutSelectionFragment onNumberAdded() - Added New number", null,
				null, true, false)
		val phone = Phone()
		phone.countryCode = countryCode
		phone.number = phoneNumber
		 addPhoneNumber = isAddPhoneNumber(countryCode, phoneNumber, mPhoneNumberList)
		if (addPhoneNumber) {
			mPhoneNumberList.add(0, phone)
			mSelectedPhoneNumber = phone
			if (mDialInNumberAdapter == null) {
				initializeAdapter()
			} else {
				mDialInNumberAdapter?.notifyDataSetChanged()
			}
		} else {
			Toast.makeText(mContext, resources.getString(R.string.number_already_exist_in_list), Toast.LENGTH_LONG).show()
		}
		if (mPhoneNumberList.isNotEmpty() && btnDialOut.visibility == View.INVISIBLE) {
			btnDialOut.visibility = View.VISIBLE
		}
	}

	companion object {
		private val TAG = DialOutSelectionFragment::class.java.simpleName

		fun newInstance(): DialOutSelectionFragment {
			return DialOutSelectionFragment()
		}

		fun isAddPhoneNumber(code: String, phoneNumber: String, phoneNumberList: List<Phone>): Boolean {
			var countryCode = code
			var addPhoneNumber = false
			countryCode = removePlusSign(countryCode)
			val numberToBeAdded = countryCode + phoneNumber
			if (phoneNumberList.isNotEmpty()) {
				for (phoneInfo in phoneNumberList) {
					var cntryCode = phoneInfo.countryCode
					val phnNumber = phoneInfo.number
					cntryCode = removePlusSign(cntryCode)
					val existingNumber = cntryCode + phnNumber
					if (existingNumber.equals(numberToBeAdded, ignoreCase = true)) {
						addPhoneNumber = false
						break
					} else {
						addPhoneNumber = true
					}
				}
			} else {
				addPhoneNumber = true
			}
			return addPhoneNumber
		}

		fun removePlusSign(code: String): String {
			var countryCode = code
			if (countryCode.contains(AppConstants.STRING_PLUS)) {
				countryCode = countryCode.substring(1)
			}
			return countryCode
		}
	}
}
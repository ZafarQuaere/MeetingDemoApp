package com. pgi.convergencemeetings.meeting.gm5.ui.audio

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.common.base.Strings
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.meeting.BaseMeetingActivity
import com.pgi.logging.enums.Interactions
import kotlinx.android.synthetic.main.dial_in_search_activity.*

/**
 * Created by ashwanikumar on 12/5/2017.
 */
class AddNumberDialogFragment : DialogFragment() {
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mActivity: BaseMeetingActivity? = null
	@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
	var mAddNumberDialogListener: AddNumberDialogListener? = null

  @BindView(R.id.et_cntry_code)
	lateinit var etCountryCode: EditText

  @BindView(R.id.et_phone_number)
	lateinit var etPhoneNumber: EditText

  @BindView(R.id.btn_cancel)
	lateinit var btnCancel: Button

  @BindView(R.id.btn_add_number)
	lateinit var btnAdd: Button
	var isValidPhNum = false
	var isValidCntryCode = false
	interface AddNumberDialogListener {
		fun onNumberAdded(countryCode: String, phoneNumber: String)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		NewRelic.setInteractionName(Interactions.ADD_DIALOUT_NUMBER.interaction)
	}

	fun initAddNumberDialog(activity: BaseMeetingActivity, addNumberDialogListener: AddNumberDialogListener) {
		mActivity = activity
		mAddNumberDialogListener = addNumberDialogListener
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.add_number_view, container, false)
		ButterKnife.bind(this, view)
		dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		etCountryCode.requestFocusFromTouch();
		etCountryCode.addTextChangedListener(object: TextWatcher {override fun afterTextChanged(s: Editable?) {
               //afterTextChanged
		}

			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
				// beforeTextChanged
			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				if(s.isNotEmpty() && s.length < AppConstants.COUNTRYCODE_LENGTH){
					validateCountryCode(s.toString())
					if(isValidPhNum){
						mActivity?.resources?.getColor(R.color.primary_color_500)?.let { btnAdd.setTextColor(it) }
						btnAdd.isEnabled = true
					}
				} else {
					mActivity?.resources?.getColor(R.color.grayMedium)?.let { btnAdd.setTextColor(it) }
					btnAdd.isEnabled = false
				}
			}
		})

		etPhoneNumber.addTextChangedListener(object: TextWatcher {override fun afterTextChanged(s: Editable?) {
			//afterTextChanged
		}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
				// beforeTextChanged
			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				if(s.isNotEmpty() && s.length >= AppConstants.PHONENUMBER_LENGTH){
					validatePhoneNumber(s.toString())
				} else {
					mActivity?.resources?.getColor(R.color.grayMedium)?.let { btnAdd.setTextColor(it) }
					btnAdd.isEnabled = false
					isValidPhNum = false
				}
			}
		})
		return view
	}

	@OnClick(R.id.btn_add_number)
	fun onAddButtonClicked() {
		val countryCode = etCountryCode.text.toString()
		val phoneNumber = etPhoneNumber.text.toString()
				mAddNumberDialogListener?.onNumberAdded(countryCode, phoneNumber)
				context?.let {
					val mInputMethodManager = it.getSystemService(Activity.INPUT_METHOD_SERVICE)
					if (mInputMethodManager != null && mInputMethodManager is InputMethodManager)
					{
						mInputMethodManager.hideSoftInputFromWindow(etPhoneNumber.windowToken, 0)
					dismiss()
				   }
				}
	}

	fun validatePhoneNumber(phoneNumber: String): Boolean {
		val length = phoneNumber.length
		isValidPhNum = false
		if (length in 6..16) {
			if(validateCountryCode(etCountryCode.text.toString())){
				mActivity?.resources?.getColor(R.color.primary_color_500)?.let { btnAdd.setTextColor(it) }
				isValidPhNum = true
				btnAdd.isEnabled = true
			} else {
				mActivity?.resources?.getColor(R.color.grayMedium)?.let { btnAdd.setTextColor(it) }
				isValidPhNum = false
				btnAdd.isEnabled = false
			}
		}
		return isValidPhNum
	}

	fun validateCountryCode(code: String): Boolean {
		var countryCode = code
		if (countryCode.contains(AppConstants.STRING_PLUS)) {
			countryCode = countryCode.substring(1)
		}
		val length = countryCode.length
		isValidCntryCode = false
		if (length in 1..4) {
			isValidCntryCode = true
		}
		return isValidCntryCode
	}

	@OnClick(R.id.btn_cancel)
	fun onCancelClicked() {
		val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
		inputMethodManager.hideSoftInputFromWindow(etPhoneNumber.windowToken, 0)
		dismiss()
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		return object : Dialog(this.activity!!, theme) {
			override fun onBackPressed() {
				dismiss()
			}
		}
	}
}
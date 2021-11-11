package com.pgi.convergencemeetings.leftnav.settings.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.common.base.Strings
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.ElkTransactionIDUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.base.services.retry_callbacks.RetryStatus
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue

/**
 * Activity to update the user name form settings screen. User can update his first name and last name
 * which will be displayed for the user while joining the meeting.
 */
class UpdateNameActivity : BaseActivity(), UpdateNameContract.view {
  @BindView(R.id.et_first_name)
	lateinit var etFirstName: EditText

  @BindView(R.id.et_last_name)
	lateinit var etLastName: EditText

  @BindView(R.id.btn_update)
	lateinit var btnUpdate: Button

  @BindView(R.id.progress_update_name)
	lateinit var pbUpdateName: ProgressBar

  @BindView(R.id.tv_err)
	lateinit var tvErrMsg: TextView

	private var mClientInfoDoaUtils = ClientInfoDaoUtils.getInstance()
	private var mAppAuthUtils = AppAuthUtils.getInstance()
	private lateinit var mPresenter: UpdateNameContract.presenter
	private var mClientId: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setInterActionName(Interactions.UPDATE_NAME.interaction)
		super.initRetryCallBacks()
		setContentView(R.layout.update_name_activity)
		mPresenter = UpdateNamePresenter(this)
		initViews()
	}

	private fun initViews() {
		ButterKnife.bind(this)
		mClientInfoDoaUtils.refereshClientInfoResultDao()
		populateClientInformation()
	}

	private fun populateClientInformation() {
		etFirstName.setText(firstName)
		etLastName.setText(lastName)
		mClientId = mClientInfoDoaUtils.clientId
	}

	//Getting first name from client info saved in cache.
	private val firstName: String?
		get() {
			var firstName = mClientInfoDoaUtils.firstName
			if (firstName == null) {
				firstName = mAppAuthUtils.firstName
			}
			return firstName
		}

	//Getting last name from client info saved in cache.
	private val lastName: String?
		get() {
			var lastName = mClientInfoDoaUtils?.lastName
			if (lastName == null) {
				lastName = mAppAuthUtils?.lastName
			}
			return lastName
		}

	/**
	 * Click event handler to update the user's name.
	 */
	@OnClick(R.id.btn_update)
	fun onUpdateNameClicked() {
		//call presenter here
		val firstName = etFirstName.text.toString()
		val lastName = etLastName.text.toString()
		val missingFirstName = Strings.isNullOrEmpty(firstName)
		val missingLastName = Strings.isNullOrEmpty(lastName)
		if (mClientId != null && !missingFirstName && !missingLastName) {
			hideNameValidationErrMsg()
			val icNormal = resources.getDrawable(R.drawable.update_name_et_bg, this.theme)
			etFirstName.background = icNormal
			etLastName.background = icNormal
			CommonUtils.hideKeyboard(this)
			showProgress()
			mClientId?.let {
				mPresenter.requestNameUpdate(it, firstName, lastName)
			}
		} else {
			if (mClientId == null) {
				tvErrMsg.text = this.getString(R.string.unable_to_update_information)
			} else {
				// update error states
				val icErr = resources.getDrawable(R.drawable.update_name_et_bg_err, this.theme)
				val icNormal = resources.getDrawable(R.drawable.update_name_et_bg, this.theme)
				if (missingFirstName) {
					etFirstName.background = icErr
					tvErrMsg.text = this.getString(R.string.please_provide_a_first_name)
				} else {
					etFirstName.background = icNormal
				}
				if (missingLastName) {
					etLastName.background = icErr
					tvErrMsg.text = this.getString(R.string.please_provide_a_last_name)
				} else {
					etLastName.background = icNormal
				}
				if (missingFirstName && missingLastName) {
					tvErrMsg.text = this.getString(R.string.please_provide_a_first_and_last_name)
				}
			}
			showNameValidationErrMsg()
		}
	}

	private fun hideNameValidationErrMsg() {
		tvErrMsg.visibility = View.INVISIBLE
	}

	private fun showNameValidationErrMsg() {
		tvErrMsg.visibility = View.VISIBLE
	}

	override fun onNameUpdateSuccess() {
		mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.UPDATE_NAME, "UpdateNameActivity " +
				"onNameUpdateSuccess() - Success updating name", null, null, true, false)
	}

	override fun onNameUpdateError() {
		mlogger.error(TAG, LogEvent.APP_VIEW, LogEventValue.UPDATE_NAME, "UpdateNameActivity " +
				"onNameUpdateError() - Failed updating name", null, null, true, false)
		hideProgress()
	}

	override fun showProgress() {
		btnUpdate.text = AppConstants.EMPTY_STRING
		pbUpdateName.visibility = View.VISIBLE
	}

	override fun hideProgress() {
		btnUpdate.text = getString(R.string.btn_update_name)
		pbUpdateName.visibility = View.GONE
	}

	override fun sendUpdateNameBroadcast() {
		val intent = Intent(AppConstants.UPDATE_NAME_BROADCAST)
		// You can also include some extra data.
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
		showNameUpdateDialog()
	}

	override fun onServiceRetryFailed(retryStatus: RetryStatus) {
		super.onServiceRetryFailed(retryStatus)
	}

	@OnClick(R.id.btn_close)
	fun onBackBtnClick() {
		finish()
	}

	/**
	 * Setting presenter.
	 *
	 * @param presenter the presenter
	 */
	@VisibleForTesting
	fun setPresenter(presenter: UpdateNamePresenter) {
		mPresenter = presenter
	}

	private fun showNameUpdateDialog() {
		try {
			if (!isFinishing) {
				val builder = AlertDialog.Builder(this)
				builder.setMessage(this.getString(R.string.display_name_update_sucess_msg))
				builder.setPositiveButton(R.string.str_ok) { dialog, which ->
					dialog.dismiss()
					finish()
					ElkTransactionIDUtils.resetTransactionId()
				}
				val alertDialog = builder.create()
				alertDialog.show()
				alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.primary_color_500))
			}
		} catch (ex: Exception) {
			mlogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.UPDATE_NAME, "UpdateNameActivity " +
					"showNameUpdateDialog() - Failed Showing Dialog", null, null, true, false)
		}
	}

	companion object {
		private val TAG = UpdateNameActivity::class.java.simpleName
	}
}
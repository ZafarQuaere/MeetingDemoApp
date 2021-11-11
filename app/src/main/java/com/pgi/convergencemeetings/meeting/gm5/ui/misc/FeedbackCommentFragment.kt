package com.pgi.convergencemeetings.meeting.gm5.ui.misc

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants.*
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.BuildConfig
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.home.ui.AppBaseLayoutActivity
import com.pgi.convergencemeetings.meeting.gm5.data.repository.feedback.SalesForceRepository
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.convergencemeetings.utils.ClientInfoDaoUtils
import com.pgi.logging.Logger
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue
import java.lang.ref.WeakReference
import java.util.*


class FeedbackCommentFragment : Fragment() {
    private val logger: Logger = CoreApplication.mLogger
    private val TAG = FeedbackCommentFragment::class.java.simpleName

    @BindView(R.id.feedbackCommentEditText)
    lateinit var commentEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        NewRelic.setInteractionName(Interactions.FEEDBACK.interaction)
        val view = inflater.inflate(R.layout.fragment_feedback_comment, container, false)
        ButterKnife.bind(this, view)
        commentEditText.setSelection(0,commentEditText.text.toString().length)
        commentEditText.requestFocus()
        return view
    }

     @OnClick(R.id.feedback_comment_back_btn)
     fun onFeedbackCommentBackBtn() {
        (activity as? WebMeetingActivity)?.openFeedbackFragmentOnBack()
     }

    @OnClick(R.id.btn_send_feedback)
    fun onSendFeedbackCommentClick() {
        //Here we are sending Negative feedback logs to Kibana and MixPanel and also create Sales force ticket
        if(!logger.meetingModel.uniqueMeetingId.isNullOrEmpty()) {
            logger.mixPanelEndOfMeetingFeedback.rating = NEGATIVE
            var issueDescription = commentEditText.text.toString().trim()
            logger.mixPanelEndOfMeetingFeedback.issueDescription =  issueDescription
            logger.info(TAG, LogEvent.MIXPANEL_EVENT, LogEventValue.MIXPANEL_END_OF_MEETING_FEEDBACK,MIXPANEL_EVENT + LogEventValue.MIXPANEL_END_OF_MEETING_FEEDBACK.value,
                    null, null, false, true)
            //create salesforce ticket when issue not blank
            if(issueDescription.isNotEmpty()) {
                createSalesForceTicket(issueDescription)
            }
            logger.meetingModel.uniqueMeetingId = null
        }
        CommonUtils.showEndMeetingThankYouSnackBar(true)
        launchApplicationFlow()
        logger.clearModels()
    }

    private fun createSalesForceTicket(issueDescription: String) {
        val appAuthUtils = AppAuthUtils.getInstance()
        val clientInfo = ClientInfoDaoUtils.getInstance()
        val ticketSubject = if(BuildConfig.FLAVOR.contains(LUMEN_APP)) SALES_FORCE_TICKET_SUBJECT_LUMEN else SALES_FORCE_TICKET_SUBJECT_GM
        SalesForceRepository.instance.createSalesForceTicket(
                email = trimString(appAuthUtils.emailId, 80), // max length 80
                clientId = trimString(clientInfo.clientId, 100),
                confId = trimString(clientInfo.conferenceId, 100),
                name = CommonUtils.getFullName(appAuthUtils.firstName, appAuthUtils.lastName),
                userType = determineUserTypeForSalesforce(appAuthUtils.emailId),
                followupEmail = trimString(appAuthUtils.emailId, 200),
                osLanguage = Locale.getDefault().displayLanguage,
                productVersion = CommonUtils.getAppVersion(WeakReference(context)),
                webUrl = logger.meetingModel.furl ?: "",
                requesterName = CommonUtils.getFullName(appAuthUtils.firstName, appAuthUtils.lastName),
                meetingLang = "English",
                subject = ticketSubject,
                description = issueDescription,
                region = determineRegion()
        )
    }

    @OnClick(R.id.textCancel)
     fun onCancelClick() {
        launchApplicationFlow()
        logger.clearModels()
     }

    private fun launchApplicationFlow() {
        val navigationIntent = Intent()
        activity?.let { navigationIntent.setClass(it, AppBaseLayoutActivity::class.java) }
        startActivity(navigationIntent)
        activity?.finish()
    }

    private fun determineRegion(): String {
        if(logger.attendeeModel.meetingServer?.contains("web-na") == true) {
            return MEETING_SERVER_REGION_NA
        } else if(logger.attendeeModel.meetingServer?.contains("web-emea") == true) {
            return MEETING_SERVER_REGION_EMEA
        } else {
            return MEETING_SERVER_REGION_APAC
        }
    }

    private fun determineUserTypeForSalesforce(email: String): String {
        if(email.split("@")[1] == "pgi.com") {
            return USER_TYPE_INTERNAL_EMPLOYEE
        } else if(logger.attendeeModel.roomRole == "Host") {
            return HOST
        } else {
            return USER_TYPE_PARTICIPANT
        }
    }

    private fun trimString(value: String?, maxLength: Int): String {
        value?.let {
            if(it.length > maxLength) {
                return it.substring(0, maxLength -1)
            }
            return it
        }
        return ""
    }
}
package com.pgi.convergence.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue

class ShareSheetBroadcastReceiverInOutMeeting : BroadcastReceiver() {
    val TAG = ShareSheetBroadcastReceiverInOutMeeting::class.java.simpleName
    override fun onReceive(context: Context, intent: Intent) {
        var meetingFrom = intent.getStringExtra(AppConstants.MEETING)
        val clickedComponent: ComponentName = intent.getParcelableExtra(Intent.EXTRA_CHOSEN_COMPONENT)
        CoreApplication.mLogger.mixpanelShareMeetingInformation.shareOptionSelected = clickedComponent.packageName
        CoreApplication.mLogger.mixpanelShareMeetingInformation.actionSource = meetingFrom
        CoreApplication.mLogger.info(TAG, LogEvent.MIXPANEL_METRICS_EVENT_NAME, LogEventValue.MIXPANEL_SHARE_MEETING_INFORMATION, AppConstants.MIXPANEL_EVENT + LogEventValue.MIXPANEL_SHARE_MEETING_INFORMATION.value,
                null, null, false, true)
    }
}
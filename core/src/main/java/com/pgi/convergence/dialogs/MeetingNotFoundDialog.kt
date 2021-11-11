package com.pgi.convergence.dialogs

import android.app.AlertDialog
import android.content.Context
import com.convergence.core.R

class MeetingNotFoundDialog() {
    fun showInvalidConferenceAlert(context: Context) {
        val dialog = AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.invalid_url_meeting_prompt_title))
                .setMessage(context.getString(R.string.invalid_url_meeting_prompt_message))
                .setPositiveButton(context.getString(R.string.dialog_ok), null).create()

        dialog.show()
    }

}
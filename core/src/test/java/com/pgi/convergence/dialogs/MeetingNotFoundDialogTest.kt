package com.pgi.convergence.dialogs

import android.content.Context
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
class MeetingNotFoundDialogTest {

    val meetingNotFoundDialog = MeetingNotFoundDialog()
    internal lateinit var context: Context
    @Before
    fun setup() {
        context = RuntimeEnvironment.application.applicationContext

    }
    @Test
    fun testShowInvalidConferenceAlert() {
        meetingNotFoundDialog.showInvalidConferenceAlert(context)
        assertTrue(ShadowAlertDialog.getShownDialogs()[0].isShowing)
    }
}
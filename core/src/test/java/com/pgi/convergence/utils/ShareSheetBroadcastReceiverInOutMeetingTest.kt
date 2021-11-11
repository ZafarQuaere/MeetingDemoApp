package com.pgi.convergence.utils

import android.content.ComponentName
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.TestLogger
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.broadcastreceivers.ShareSheetBroadcastReceiverInOutMeeting
import com.pgi.convergence.constants.AppConstants
import io.mockk.MockKAnnotations
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.serialization.UnstableDefault
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@UnstableDefault
@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ShareSheetBroadcastReceiverInOutMeetingTest {
    @Before()
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        CoreApplication.mLogger = TestLogger()
        CoreApplication.appContext = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun `test shareSheetBroadcastReceiverInMeeting`() {
        val shareSheetBroadcastReceiverInOutMeeting = ShareSheetBroadcastReceiverInOutMeeting()
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_CHOSEN_COMPONENT, ComponentName(AppConstants.TEST_PACKAGE_NAME, AppConstants.TEST_PACKAGE_NAME))
        shareSheetBroadcastReceiverInOutMeeting.onReceive(CoreApplication.appContext, intent)
    }
}
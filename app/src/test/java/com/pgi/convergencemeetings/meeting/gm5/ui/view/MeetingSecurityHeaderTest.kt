package com.pgi.convergencemeetings.meeting.gm5.ui.view

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.application.TestApplication
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(application = TestApplication::class)
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class MeetingSecurityHeaderTest {

    private var headerView: MeetingSecurityHeader? = null

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        headerView = MeetingSecurityHeader(context)
    }

    @Test
    fun `test headerView not null`() {
        Assert.assertNotNull(headerView)
    }

    @Test
    fun `test Lock Screen Option Exists`() {
        Assert.assertNotNull(headerView?.lockRoomView)
        Assert.assertEquals(headerView?.lockRoomView?.tvTitle?.text, headerView?.context?.getString(R.string.lock_room))
        Assert.assertEquals(headerView?.lockRoomView?.tvDescription?.text, headerView?.context?.getString(R.string.lock_room_description))
        Assert.assertNotNull(headerView?.lockRoomView?.swSecurityOption)
    }

    @Test
    fun `test Waiting Room Option Exists`() {
        Assert.assertNotNull(headerView?.waitingRoomView)
        Assert.assertEquals(headerView?.waitingRoomView?.tvTitle?.text, headerView?.context?.getString(R.string.enable_waiting_room))
        Assert.assertEquals(headerView?.waitingRoomView?.tvDescription?.text, headerView?.context?.getString(R.string.waiting_room_description))
        Assert.assertNotNull(headerView?.waitingRoomView?.swSecurityOption)
    }

    @Test
    fun `test Restrict Sharing Option Exists`() {
        Assert.assertNotNull(headerView?.restrictSharingView)
        Assert.assertEquals(headerView?.restrictSharingView?.tvTitle?.text, headerView?.context?.getString(R.string.restrict_sharing))
        Assert.assertEquals(headerView?.restrictSharingView?.tvDescription?.text, headerView?.context?.getString(R.string.restrict_sharing_description))
        Assert.assertNotNull(headerView?.restrictSharingView?.swSecurityOption)
    }

    @Test
    fun `test Waiting Room Fragment Exists`() {
        Assert.assertNotNull(headerView?.rlWaitingRoomGuestFragment)
        Assert.assertEquals(headerView?.tvWaitingRoomGuestListHeader?.text, headerView?.context?.getString(R.string.waiting_room_fragment))
        Assert.assertNotNull(headerView?.tvWaitingRoomGuestListCount)
    }
}
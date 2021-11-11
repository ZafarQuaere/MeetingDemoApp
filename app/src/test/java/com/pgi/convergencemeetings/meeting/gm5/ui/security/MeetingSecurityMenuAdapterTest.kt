package com.pgi.convergencemeetings.meeting.gm5.ui.security

import android.content.Context
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.base.ui.retry.RetryFailedContactor
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.view.MeetingSecurityHeader
import com.squareup.picasso.Picasso
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockkClass
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(application = TestApplication::class)
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class MeetingSecurityMenuAdapterTest : RobolectricTest() {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    private var mMeetingSecurityHeader: MeetingSecurityHeader? = null
    private var mUserList: ArrayList<User> = ArrayList()
    private var mMenuSecurityMenuAdapter: MeetingSecurityMenuAdapter? = null

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        mockkStatic(Picasso::class)
        val picassoMock = mockkClass(Picasso::class, relaxed = true)
        coEvery {
            Picasso.get()
        } returns picassoMock
        val context = ApplicationProvider.getApplicationContext<Context>()
        mMeetingSecurityHeader = MeetingSecurityHeader(context)
        val user1 = User(id = "92ubinoow077xrml2audnsb61", firstName = "h", lastName = "h", name = "h h", initials = "HH", email = "rajni.j@hcl.com", profileImage = "https://sample.jpg", roomRole = "WAITING_GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false)
        val user2 = User(id = "92ubinoow077xrml2audnsb62", firstName = "h", lastName = "h", name = "h h", initials = "HH", email = "rajni.j@hcl.com", profileImage = "https://sample.jpg", roomRole = "WAITING_GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false)
        mUserList.add(user1)
        mUserList.add(user2)
        mMenuSecurityMenuAdapter = mMeetingSecurityHeader?.let { MeetingSecurityMenuAdapter(context, CoreApplication.mLogger, it, mUserList) }
    }

    @After
    fun tearDown() {
        mMenuSecurityMenuAdapter = null
    }

    @Test
    fun `test testGetItemCount`() {
        Assert.assertEquals(3, mMenuSecurityMenuAdapter?.itemCount)  //+1 for header
    }

    @Test
    fun `test testGetViewTypeHeader`() {
        val viewType = 0 // 0th location set for header
        Assert.assertEquals(mMenuSecurityMenuAdapter?.MEETING_SECURITY_HEADER_TYPE, mMenuSecurityMenuAdapter?.getItemViewType(viewType))
    }

    @Test
    fun `test testGetViewTypeList`() {
        val viewType = 1 //1 for guest list but with one condition userList not empty
        Assert.assertFalse(mUserList.isEmpty())
        Assert.assertEquals(mMenuSecurityMenuAdapter?.WAITING_GUEST_LIST_TYPE, mMenuSecurityMenuAdapter?.getItemViewType(viewType))
    }

    @Test
    fun `test testGetViewTypeEmptyGuestList`() {
        val viewType = 2 //2 for empty guest list when userList is empty
        mUserList.clear()
        Assert.assertTrue(mUserList.isEmpty())
        Assert.assertEquals(mMenuSecurityMenuAdapter?.WAITING_GUEST_EMPTY_MESSAGE_TYPE, mMenuSecurityMenuAdapter?.getItemViewType(viewType))
    }

    @Test
    fun `test userForIndex`() {
        //when pos is 1
        var pos = 1
        mMenuSecurityMenuAdapter?.userForIndex(pos)
        Assert.assertFalse(mUserList.isEmpty())
        Assert.assertEquals(mUserList[pos-1],mUserList[0])
        //when pos is 2
        pos= 2
        mMenuSecurityMenuAdapter?.userForIndex(pos)
        Assert.assertFalse(mUserList.isEmpty())
        Assert.assertEquals(mUserList[pos-1],mUserList[1])
        //when list is empty and pos is anything
        mUserList.clear()
        mMenuSecurityMenuAdapter?.userForIndex(pos)
        Assert.assertTrue(mUserList.isEmpty())
    }

    @Test
    fun `test guestListItemHolder not null`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        // viewType = 0, for header
        val headerViewHolder: MeetingSecurityMenuAdapter.HeaderViewHolder = mMenuSecurityMenuAdapter?.onCreateViewHolder(recyclerView, 0) as MeetingSecurityMenuAdapter.HeaderViewHolder
        mMenuSecurityMenuAdapter?.onBindViewHolder(headerViewHolder, 0)
        Assert.assertNotNull(headerViewHolder)
        // viewType =1, for Empty guest list
        val emptyGuestListHolder: MeetingSecurityMenuAdapter.EmptyGuestListHolder = mMenuSecurityMenuAdapter?.onCreateViewHolder(recyclerView, 1) as MeetingSecurityMenuAdapter.EmptyGuestListHolder
        // if enable waiting room option is enabled- show guest list holder
        mMeetingSecurityHeader?.waitingRoomView?.swSecurityOption?.isChecked = true
        mMenuSecurityMenuAdapter?.onBindViewHolder(emptyGuestListHolder, 1)
        Assert.assertNotNull(emptyGuestListHolder.mEmptyGuestView)
        Assert.assertTrue(emptyGuestListHolder.mEmptyGuestView.visibility == View.VISIBLE)
        Assert.assertTrue(mMeetingSecurityHeader?.rlWaitingRoomGuestFragment?.visibility == View.VISIBLE)

        // if enable waiting room option is disabled- hide guest list holder
        mMeetingSecurityHeader?.waitingRoomView?.swSecurityOption?.isChecked = false
        mMeetingSecurityHeader?.rlWaitingRoomGuestFragment?.visibility = View.GONE
        mMenuSecurityMenuAdapter?.onBindViewHolder(emptyGuestListHolder, 1)
        Assert.assertNotNull(emptyGuestListHolder.mEmptyGuestView)
        Assert.assertTrue(emptyGuestListHolder.mEmptyGuestView.visibility == View.GONE)
        Assert.assertTrue(mMeetingSecurityHeader?.rlWaitingRoomGuestFragment?.visibility == View.GONE)

        // viewType =2, for guest list
        val guestListHolder: MeetingSecurityMenuAdapter.GuestListItemHolder = mMenuSecurityMenuAdapter?.onCreateViewHolder(recyclerView, 2) as MeetingSecurityMenuAdapter.GuestListItemHolder
        mMenuSecurityMenuAdapter?.onBindViewHolder(guestListHolder, 1)
        Assert.assertNotNull(guestListHolder)
        Assert.assertFalse(mUserList.isEmpty())

        Assert.assertEquals(guestListHolder.mGuestName.text, mUserList[0].name)
        Assert.assertEquals(guestListHolder.mGuestNameInitials.text, mUserList[0].initials)
        Assert.assertEquals(guestListHolder.mGuestNameInitials.visibility, View.VISIBLE)
        Assert.assertEquals(guestListHolder.mProfilePicGuest.visibility, View.INVISIBLE)
        Assert.assertNotNull(mUserList[0].profileImage)

        val user1 = User(id = "92ubinoow077xrml2audnsb61", firstName = "h", lastName = "h", name = "h h", initials = "#", email = "rajni.j@hcl.com", profileImage = "https://sample.jpg", roomRole = "WAITING_GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false)
        mUserList.clear()
        mUserList.add(user1)
        val guestListHolder1: MeetingSecurityMenuAdapter.GuestListItemHolder = mMenuSecurityMenuAdapter?.onCreateViewHolder(recyclerView, 2) as MeetingSecurityMenuAdapter.GuestListItemHolder
        mMenuSecurityMenuAdapter?.onBindViewHolder(guestListHolder1, 1)
        Assert.assertNotNull(guestListHolder1)
        Assert.assertFalse(mUserList.isEmpty())

        Assert.assertEquals(guestListHolder1.mGuestName.text, mUserList[0].name)
        Assert.assertEquals(guestListHolder1.mGuestNameInitials.text, mUserList[0].initials)
        Assert.assertEquals(guestListHolder1.mGuestNameInitials.visibility, View.INVISIBLE)
        Assert.assertEquals(guestListHolder1.mProfilePicGuest.visibility, View.VISIBLE)
        Assert.assertNotNull(mUserList[0].profileImage)
    }

}
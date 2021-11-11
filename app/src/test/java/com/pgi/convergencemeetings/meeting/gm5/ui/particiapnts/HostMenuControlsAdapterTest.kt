package com.pgi.convergencemeetings.meeting.gm5.ui.particiapnts

import android.content.Context
import android.widget.ListView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.enums.HostControlsEnum
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.ui.BaseMeetingViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.models.HostControlsModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.mock
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class HostMenuControlsAdapterTest: RobolectricTest() {

    lateinit var mFragment: ParticipantListFragment
    lateinit var mMeetingUserViewModel: MeetingUserViewModel
    lateinit var mBaseViewModel: BaseMeetingViewModel
    private var mUserList: ArrayList<User> = ArrayList()
    private var participantAdapter: WebParticipantListAdapter? = null
    private var menuAdapter: HostMenuControlsAdapter? = null
    private var hostControlList: ArrayList<HostControlsModel> = ArrayList()
    @MockK
    private var mWebMeetingActivity: WebMeetingActivity? = null
    @MockK
    lateinit var mockMeetingUserViewModel: MeetingUserViewModel

    @Before
    fun `set up`() {
        MockKAnnotations.init(this, relaxed = true)
        MockitoAnnotations.initMocks(this)
        mFragment = mock(ParticipantListFragment::class)
        mMeetingUserViewModel = mock(MeetingUserViewModel::class)
        mBaseViewModel = mock(BaseMeetingViewModel::class)
        val controller = Robolectric.buildActivity(WebMeetingActivity::class.java)
        mWebMeetingActivity = controller.get()
        val user1 = User(id = "12345", firstName = "Z", lastName = "I", name = "Z I", initials = "HH", email = "imam.z@hcl.com", profileImage = "https://sample.jpg", roomRole = "GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false)
        val user2 = User(id = "11223", firstName = "S", lastName = "B", name = "S B", initials = "HH", email = "imam.z@hcl.com", profileImage = "https://sample.jpg", roomRole = "HOST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false)
        mUserList.add(user1)
        mUserList.add(user2)
        val context = ApplicationProvider.getApplicationContext<Context>()
        participantAdapter = WebParticipantListAdapter(mMeetingUserViewModel, CoreApplication.appContext, mUserList,false)!!
        val bottomSheetDialog = BottomSheetDialog(context)
        val resourceLayout = R.layout.menu_item_host_control
        menuAdapter = HostMenuControlsAdapter(bottomSheetDialog, user1, mMeetingUserViewModel, context, resourceLayout, getHostControlList())
    }

    private fun getHostControlList(): MutableList<HostControlsModel> {
        val hostControlsModel = HostControlsModel()
        hostControlsModel.controlName = AppConstants.MENU_MUTE
        hostControlsModel.hostControlType = HostControlsEnum.MUTE
        hostControlList.add(AppConstants.ZERO,hostControlsModel)
        val hostControlsModel1 = HostControlsModel()
        hostControlsModel1.controlName = AppConstants.MENU_UNMUTE
        hostControlsModel1.hostControlType = HostControlsEnum.UNMUTE
        hostControlList.add(AppConstants.ONE,hostControlsModel1)
        val hostControlsModel2 = HostControlsModel()
        hostControlsModel2.controlName = AppConstants.MENU_PROMOTE
        hostControlsModel2.hostControlType = HostControlsEnum.PROMOTE
        hostControlList.add(AppConstants.TWO,hostControlsModel2)
        val hostControlsModel3 = HostControlsModel()
        hostControlsModel3.controlName = AppConstants.MENU_DEMOTE
        hostControlsModel3.hostControlType = HostControlsEnum.DEMOTE
        hostControlList.add(AppConstants.THREE,hostControlsModel3)
        val hostControlsModel4 = HostControlsModel()
        hostControlsModel4.controlName = AppConstants.MENU_DISMISS
        hostControlsModel4.hostControlType = HostControlsEnum.DISMISS
        hostControlList.add(AppConstants.FOUR,hostControlsModel4)
        val hostControlsModel5 = HostControlsModel()
        hostControlsModel5.controlName = AppConstants.MENU_PRIVATE_CHAT_WITH
        hostControlsModel5.hostControlType = HostControlsEnum.PRIVATE_CHAT_WITH
        hostControlList.add(AppConstants.FIVE,hostControlsModel5)
        return hostControlList
    }

    @After
    fun tearDown() {
        participantAdapter = null
        menuAdapter = null
    }

    @Test
    fun `test getViewItem `() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val listView = ListView(context)
        val getView = menuAdapter?.getView(AppConstants.ZERO, null, listView)
        Assert.assertNotNull(getView)
        val getView1 = menuAdapter?.getView(AppConstants.ONE, null, listView)
        Assert.assertNotNull(getView1)
        val getView2 = menuAdapter?.getView(AppConstants.TWO, null, listView)
        Assert.assertNotNull(getView2)
        val getView3 = menuAdapter?.getView(AppConstants.THREE, null, listView)
        Assert.assertNotNull(getView3)
        val getView4 = menuAdapter?.getView(AppConstants.FOUR, null, listView)
        Assert.assertNotNull(getView4)
        val getView5 = menuAdapter?.getView(AppConstants.FIVE, null, listView)
        Assert.assertNotNull(getView5)
        Assert.assertEquals(hostControlList[AppConstants.FIVE].controlName, AppConstants.MENU_PRIVATE_CHAT_WITH)
        Assert.assertThat(HostControlsEnum.valueOf("PRIVATE_CHAT_WITH"),notNullValue())
        Assert.assertEquals(hostControlList[AppConstants.FIVE].hostControlType, HostControlsEnum.PRIVATE_CHAT_WITH)
        val privateMenuModel = HostControlsModel()
        privateMenuModel.controlName = null
        hostControlList.add(AppConstants.FIVE, privateMenuModel)
        val getPrivateMenu = menuAdapter?.getView(AppConstants.FIVE, null, listView)
        Assert.assertNotNull(getPrivateMenu)
        Assert.assertNotEquals(hostControlList[AppConstants.FIVE].controlName, AppConstants.MENU_PRIVATE_CHAT_WITH)
    }

    @Test
    fun `test onShowDetailPage`() {
        Assert.assertNotNull(mWebMeetingActivity)
        every {
            mockMeetingUserViewModel.isOpenChatFragment.value
        } returns false
        menuAdapter?.mMeetingUserViewModel = mockMeetingUserViewModel
        menuAdapter?.onShowChatDetailPage(User())
        mWebMeetingActivity = null
        Assert.assertNull(mWebMeetingActivity)
        val user1 = User(id = "12345", firstName = "Z", lastName = "I", name = "Z I", initials = "HH", email = "imam.z@hcl.com", profileImage = "https://sample.jpg", roomRole = "GUEST", promoted = false, timestamp = "2020-07-27T02:23:47.824Z", isSelf = false, isSharing = false, hasControls = true, reconnected = false, active = false)
        menuAdapter?.onShowChatDetailPage(user1)
        Assert.assertFalse(user1.name.isNullOrEmpty())
    }

    @Test
    fun `test onMenuClicked`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val listView = ListView(context)
        val getView = menuAdapter?.getView(AppConstants.FIVE, null, listView)
        Assert.assertNotNull(getView)
        every {
            mockMeetingUserViewModel.isOpenChatFragment.value
        } returns false
        menuAdapter?.mMeetingUserViewModel = mockMeetingUserViewModel
        getView?.performClick()
        menuAdapter?.mBottomSheetPopupDialog?.isShowing?.let { Assert.assertFalse(it) }
    }
}
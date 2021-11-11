package com.pgi.convergencemeetings.meeting.gm5.ui.chat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.landingpage.ui.SplashScreenActivity
import com.pgi.convergencemeetings.meeting.gm5.data.model.User
import com.pgi.convergencemeetings.meeting.gm5.di.meetingSecurityFragmentModule
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.squareup.picasso.Picasso
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@Config(application = TestApplication::class)
@RunWith(AndroidJUnit4::class)
class NewPrivateChatSelectionFragmentTest: KoinTest, RobolectricTest(){

    @MockK
    private var mWebMeetingActivity: WebMeetingActivity? = null
    @ExperimentalCoroutinesApi
    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    private lateinit var mPrivateChatSelectionFragment: NewPrivateChatSelectionFragment

    private fun launchFragment(onInstantiated: (NewPrivateChatSelectionFragment) -> Unit = {}):
            FragmentScenario<NewPrivateChatSelectionFragment> {
        return FragmentScenario.launchInContainer(NewPrivateChatSelectionFragment::class.java, null, com.pgi.convergence.agenda.R.style
                .AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as NewPrivateChatSelectionFragment
                this@NewPrivateChatSelectionFragmentTest.mPrivateChatSelectionFragment = fragment
                val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
                mPrivateChatSelectionFragment.initializeParentActivity(webMeetingActivity)
                onInstantiated(fragment)
                return fragment
            }
        })
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this,relaxed = true)
        mPrivateChatSelectionFragment = NewPrivateChatSelectionFragment()
        Dispatchers.setMain(dispatcher)
        mockkStatic(Picasso::class)
        startKoinApp()
        val controller = Robolectric.buildActivity(WebMeetingActivity::class.java)
        mWebMeetingActivity = controller.get()
        val picassoMock = mockkClass(Picasso::class, relaxed = true)
        coEvery {
            Picasso.get()
        } returns picassoMock
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    fun startKoinApp() {
        startKoin {
            modules(meetingSecurityFragmentModule)
        }
    }

    @Test
    fun `test vars not null`() {
        launchFragment()
        Assert.assertNotNull(mPrivateChatSelectionFragment.mMeetingEventsModel)
        Assert.assertNotNull(mPrivateChatSelectionFragment.mMeetingUserViewModel)
        Assert.assertNotNull(mPrivateChatSelectionFragment.rvChatParticipantList)
    }

    @Test
    fun `test onShowDetailPage`() {
        launchFragment()
        Assert.assertNotNull(mWebMeetingActivity)
        mPrivateChatSelectionFragment.onShowChatDetailPage(User())
        mWebMeetingActivity = null
        Assert.assertNull(mWebMeetingActivity)

        val user = User(name = "Test", initials = "TT", profileImage = "")
        mPrivateChatSelectionFragment.onShowChatDetailPage(user)
        mWebMeetingActivity = null
        Assert.assertNull(mWebMeetingActivity)
        mPrivateChatSelectionFragment.mMeetingUserViewModel = null
        mPrivateChatSelectionFragment.onShowChatDetailPage(user)
        Assert.assertNull(mWebMeetingActivity)
        mPrivateChatSelectionFragment.isPrivateChatEnable = false
        mPrivateChatSelectionFragment.onShowChatDetailPage(user)
        Assert.assertNotNull(mPrivateChatSelectionFragment.parentActivity)
    }

    @Test
    fun ` test update user list` () {
        launchFragment()
        val mUserList: MutableList<User> = ArrayList()
        val user1 = User( roomRole = AppConstants.GUEST, firstName = "Zafar", isSelf = false, initials = "ZI")
        val user2 = User( roomRole = AppConstants.GUEST, firstName = "Sani", isSelf = true, initials = "ZI")
        val user3 = User( roomRole = AppConstants.GUEST, firstName = "", isSelf = false, initials = "#")
        val user4 = User( roomRole = AppConstants.GUEST, firstName = "Sani", isSelf = true, initials = "#")
        mUserList.add(user1)
        mUserList.add(user2)
        mUserList.add(user3)
        mUserList.add(user4)
        mPrivateChatSelectionFragment.updateUserList(mUserList)
        Assert.assertNotNull(mPrivateChatSelectionFragment.mUserList)
        mPrivateChatSelectionFragment.mWebParticipantListAdapter = null
        mPrivateChatSelectionFragment.updateUserList(mUserList)
        Assert.assertNotNull(mPrivateChatSelectionFragment.mUserList)
        mPrivateChatSelectionFragment.mMeetingUserViewModel = null
        mPrivateChatSelectionFragment.updateUserList(mUserList)
        Assert.assertNull(mPrivateChatSelectionFragment.mMeetingUserViewModel)
    }

    @Test
    fun ` test remove fragment` () {
        launchFragment()
        mPrivateChatSelectionFragment.removeSelectPrivateChatFragment()
        Assert.assertNull(mPrivateChatSelectionFragment.addOrRemoveFragment)
    }

    @Test
    fun `update private chat status` () {
        launchFragment()
        mPrivateChatSelectionFragment.mMeetingEventsModel?.privateChatEnableStatus?.value = false
        mPrivateChatSelectionFragment.updatePrivateChatStatus(false)
        Assert.assertEquals(mPrivateChatSelectionFragment.isPrivateChatEnable,false)
        mPrivateChatSelectionFragment.updatePrivateChatStatus(true)
        Assert.assertEquals(mPrivateChatSelectionFragment.isPrivateChatEnable,true)
    }

    @Test
    fun `test initialize Parent Activity` () {
        launchFragment()
        Assert.assertNotNull(mPrivateChatSelectionFragment.mContext)
        val mainActivity = mockk<SplashScreenActivity>(relaxed = true)
        mPrivateChatSelectionFragment.initializeParentActivity(mainActivity)
        val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
        mPrivateChatSelectionFragment.initializeParentActivity(webMeetingActivity)
        Assert.assertNotNull(mPrivateChatSelectionFragment.parentActivity)
    }
}
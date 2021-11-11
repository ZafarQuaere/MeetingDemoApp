package com.pgi.convergencemeetings.meeting.gm5.ui.content

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.webkit.RenderProcessGoneDetail
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.common.features.Features
import com.pgi.convergence.common.features.FeaturesManager
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.constants.AppConstants.RETURN_0
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergence.utils.InternetConnection
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.TestLogger
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.di.featuresMangerModule
import com.pgi.convergencemeetings.leftnav.settings.ui.UpdateNameActivity
import com.pgi.convergencemeetings.meeting.gm5.data.enums.uapi.*
import com.pgi.convergencemeetings.meeting.gm5.data.model.*
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingEventViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.MeetingUserViewModel
import com.pgi.convergencemeetings.meeting.gm5.ui.WebMeetingActivity
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.squareup.picasso.Picasso
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class ContentPresentationFragmentTest: KoinTest {

	lateinit var fragment: ContentPresentationFragment
	lateinit var mockSharedPref: SharedPreferencesManager
	lateinit var mockUtils: CommonUtils
	@MockK
	lateinit var mockMeetingUserViewModel: MeetingUserViewModel
	@MockK
	lateinit var mockMeetingEventViewModel: MeetingEventViewModel
	@MockK
	lateinit var mockFeatureManager: FeaturesManager
	private var webMeetingActivity: WebMeetingActivity? = null
	private var TRANSITIONTOFULLSCREEN: String = "transitionToFullScreen"
	private fun launchFragment(onInstantiated: (ContentPresentationFragment) -> Unit = {}):
			FragmentScenario<ContentPresentationFragment> {
		return FragmentScenario.launchInContainer(ContentPresentationFragment::class.java, null, R.style.AppTheme, object : FragmentFactory() {
			override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
				val fragment = super.instantiate(classLoader, className) as ContentPresentationFragment
				this@ContentPresentationFragmentTest.fragment = fragment
				onInstantiated(fragment)
				return fragment
			}
		})
	}

	@Before
	fun `set up`() {
		MockKAnnotations.init(this, relaxed = true)
		mockkStatic(Picasso::class)
		val picassoMock = mockkClass(Picasso::class, relaxed = true)
		coEvery {
			Picasso.get()
		} returns picassoMock
		CoreApplication.mLogger = TestLogger()
		mockkStatic(SharedPreferencesManager::class)
		mockSharedPref = mockkClass(SharedPreferencesManager::class, relaxed = true)
		mockFeatureManager = mockkClass(FeaturesManager::class, relaxed = true)
		every {
			SharedPreferencesManager.getInstance()
		} returns mockSharedPref
		mockSharedPref.cloudRegion = "us-east-1"
		startKoin { modules(featuresMangerModule) }
		webMeetingActivity = Robolectric.buildActivity(WebMeetingActivity::class.java).create().resume().get()
	}

	@After
	fun `tear down`() {
		stopKoin()
	}

	@Test
	fun `test oncreate`() {
		launchFragment {
			assertNotNull(fragment)
			assertNull(fragment.context)
			assertNull(fragment.vpWebMeeting)
		}
		assertNotNull(fragment.context)
	}

	@Test
	fun `test manage topbar`() {
		launchFragment()
		fragment.meetingUserViewModel.screenSharePortrait = true
		fragment.meetingUserViewModel.screenShareLandScape = true
		fragment.meetingUserViewModel.contentPresentationActive = true
		fragment.meetingUserViewModel.screenShareFullScreen = true
		fragment.rlTopBarContainer = webMeetingActivity?.findViewById(R.id.rl_top_bar_container)
		fragment.manageTopbar()
		assertEquals(fragment.rlTopBarContainer?.visibility, View.GONE)
		fragment.meetingUserViewModel.screenSharePortrait = false
		fragment.meetingUserViewModel.screenShareLandScape = false
		fragment.meetingUserViewModel.contentPresentationActive = false
		fragment.meetingUserViewModel.screenShareFullScreen = false
		fragment.manageTopbar()
		assertEquals(fragment.rlTopBarContainer?.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.screenSharePortrait = true
		fragment.meetingUserViewModel.screenShareLandScape = false
		fragment.meetingUserViewModel.contentPresentationActive = false
		fragment.meetingUserViewModel.screenShareFullScreen = false
		fragment.manageTopbar()
		assertEquals(fragment.rlTopBarContainer?.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.screenSharePortrait = false
		fragment.meetingUserViewModel.screenShareLandScape = true
		fragment.meetingUserViewModel.contentPresentationActive = false
		fragment.meetingUserViewModel.screenShareFullScreen = false
		fragment.manageTopbar()
		assertEquals(fragment.rlTopBarContainer?.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.screenSharePortrait = false
		fragment.meetingUserViewModel.screenShareLandScape = false
		fragment.meetingUserViewModel.contentPresentationActive = true
		fragment.meetingUserViewModel.screenShareFullScreen = false
		fragment.manageTopbar()
		assertEquals(fragment.rlTopBarContainer?.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.screenSharePortrait = false
		fragment.meetingUserViewModel.screenShareLandScape = false
		fragment.meetingUserViewModel.contentPresentationActive = false
		fragment.meetingUserViewModel.screenShareFullScreen = true
		fragment.manageTopbar()
		assertEquals(fragment.rlTopBarContainer?.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.screenSharePortrait = true
		fragment.meetingUserViewModel.screenShareLandScape = false
		fragment.meetingUserViewModel.contentPresentationActive = true
		fragment.meetingUserViewModel.screenShareFullScreen = true
		fragment.manageTopbar()
		assertEquals(fragment.rlTopBarContainer?.visibility, View.GONE)
		fragment.meetingUserViewModel.screenSharePortrait = true
		fragment.meetingUserViewModel.screenShareLandScape = true
		fragment.meetingUserViewModel.contentPresentationActive = false
		fragment.meetingUserViewModel.screenShareFullScreen = true
		fragment.manageTopbar()
		assertEquals(fragment.rlTopBarContainer?.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.screenSharePortrait = true
		fragment.meetingUserViewModel.screenShareLandScape = true
		fragment.meetingUserViewModel.contentPresentationActive = true
		fragment.meetingUserViewModel.screenShareFullScreen = false
		fragment.manageTopbar()
		assertEquals(fragment.rlTopBarContainer?.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.screenSharePortrait = false
		fragment.meetingUserViewModel.screenShareLandScape = true
		fragment.meetingUserViewModel.contentPresentationActive = true
		fragment.meetingUserViewModel.screenShareFullScreen = true
		fragment.manageTopbar()
		assertEquals(fragment.rlTopBarContainer?.visibility, View.GONE)
	}

	@Test
	fun `test orientation Change listerner`() {
		launchFragment()
		fragment.orientationListener?.onOrientationChanged(0)
		assertNotNull(fragment.context)
		assertNotNull(fragment.orientationListener)
	}

	@Test
	fun `test updateTalker`() {
		launchFragment()
		val talker = ActiveTalker(
				user = User(id="1",
						firstName = "test",
						lastName = "user",
						name = "test user",
						initials = "TU"), isTalking = true)
		mockkStatic(CommonUtils::class)
		every {
			CommonUtils.isUsersLocaleJapan()
		} returns false
		fragment.updateTalker(talker)
		assertEquals(fragment.talkingState, "Speaking")
		assertEquals(fragment.tvActiveTalkerNameDelay.text, "test user")
		assertEquals(fragment.tvActiveTalkerNameDelay.visibility, View.VISIBLE)

		assertEquals(fragment.tvActiveTalkerStateDelay.text, "Speaking")
		assertEquals(fragment.tvActiveTalkerStateDelay.visibility, View.VISIBLE)
		assertEquals(fragment.textViewNoOneSpokenSS.visibility, View.GONE)

		assertEquals(fragment.textViewTalkingStateSSLandscape.text, "Speaking")
		assertEquals(fragment.textViewTalkingStateSS.text, "Speaking")
		assertEquals(fragment.textViewTalkingStateSS.visibility, View.VISIBLE)
		assertEquals(fragment.textViewTalkingStateSSLandscape.visibility, View.INVISIBLE)
		assertEquals(fragment.textViewParticipantNameLandscape.visibility, View.INVISIBLE)
		assertEquals(fragment.textViewParticipantNameSS.visibility, View.VISIBLE)

		assertEquals(fragment.circleImageViewDelay.visibility, View.GONE)
		assertEquals(fragment.tvActiveTalkerInitialsDelay.visibility, View.VISIBLE)

		assertEquals(fragment.tvActiveTalkerInitialsDelay.text, "TU")
		assertEquals(fragment.textViewParticipantNameSS.text, "test user")
		assertEquals(fragment.textViewParticipantNameLandscape.text, "test user")
	}

	@Test
	fun `test updateTalker with audio user`() {
		launchFragment()
		val talker = ActiveTalker(
				user = User(id="1",
						firstName = "test",
						lastName = "user",
						name = "test user",
						initials = "#"), isTalking = true)
		mockkStatic(CommonUtils::class)
		every {
			CommonUtils.isUsersLocaleJapan()
		} returns false
		fragment.updateTalker(talker)
		assertEquals(fragment.talkingState, "Speaking")
		assertEquals(fragment.tvActiveTalkerNameDelay.text, "test user")
		assertEquals(fragment.tvActiveTalkerNameDelay.visibility, View.VISIBLE)

		assertEquals(fragment.tvActiveTalkerStateDelay.text, "Speaking")
		assertEquals(fragment.tvActiveTalkerStateDelay.visibility, View.VISIBLE)

		assertEquals(fragment.linearNoOneHasSpokenDelay.visibility, View.VISIBLE)
		assertEquals(fragment.textViewNoOneSpokenSS.visibility, View.GONE)
		assertEquals(fragment.textViewTalkingStateSSLandscape.text, "Speaking")
		assertEquals(fragment.textViewTalkingStateSS.text, "Speaking")
		assertEquals(fragment.textViewTalkingStateSS.visibility, View.VISIBLE)
		assertEquals(fragment.textViewTalkingStateSSLandscape.visibility, View.INVISIBLE)
		assertEquals(fragment.textViewParticipantNameLandscape.visibility, View.INVISIBLE)
		assertEquals(fragment.textViewParticipantNameSS.visibility, View.VISIBLE)
		assertEquals(fragment.circleImageViewDelay.visibility, View.GONE)
		assertEquals(fragment.tvActiveTalkerInitialsDelay.visibility, View.GONE)
		assertEquals(fragment.tvActiveTalkerInitialsDelay.text, "#")
		assertEquals(fragment.textViewParticipantNameSS.text, "test user")
		assertEquals(fragment.textViewParticipantNameLandscape.text, "test user")
	}

	@Test
	fun `test updateTalker delay`() {
		launchFragment()
		val talker = ActiveTalker(
				user = User(id="1",
						firstName = "test",
						lastName = "user",
						name = "test user",
						initials = "TU"), isTalking = false)
		mockkStatic(CommonUtils::class)
		every {
			CommonUtils.isUsersLocaleJapan()
		} returns false
		fragment.updateTalker(talker)
		assertEquals(fragment.talkingState, "No one Speaking")
		assertEquals(fragment.tvActiveTalkerStateDelay.text, "No one Speaking")
	}

	@Test
	fun `test updateTalker with jp locale`() {
		launchFragment()
		val talker = ActiveTalker(
				user = User(id="1",
						firstName = "test",
						lastName = "user",
						name = "test user",
						initials = "TU"), isTalking = true)
		mockkStatic(CommonUtils::class)
		mockUtils = mockkClass(CommonUtils::class, relaxed = true)
		every {
			CommonUtils.isUsersLocaleJapan()
		} returns true
		fragment.updateTalker(talker)
		assertEquals(fragment.tvActiveTalkerNameDelay.text, "user test")
		assertEquals(fragment.tvActiveTalkerInitialsDelay.text, "UT")
		assertEquals(fragment.textViewParticipantNameSS.text, "user test")
		assertEquals(fragment.textViewParticipantNameLandscape.text, "user test")
	}

	@Test
	fun `test updateTalker with profile pic`() {
		launchFragment()
		val talker = ActiveTalker(
				user = User(id="1",
						firstName = "test",
						lastName = "user",
						name = "test user",
						initials = "TU",
						profileImage = "https://sample.jpg"),
				isTalking = true)
		mockkStatic(CommonUtils::class)
		mockUtils = mockkClass(CommonUtils::class, relaxed = true)
		every {
			CommonUtils.isUsersLocaleJapan()
		} returns true
		fragment.updateTalker(talker)
		assertEquals(fragment.circleImageViewDelay.visibility, View.VISIBLE)
		assertEquals(fragment.tvActiveTalkerInitialsDelay.visibility, View.GONE)
	}

	@Test
	fun `test handleRecording started`() {
		launchFragment()
		fragment.handleRecording(MeetingRecordStatus.RECORDING_STARTED)
		assertTrue(fragment.isRecordingOn)
	}

	@Test
	fun `test handleRecording stopped`() {
		launchFragment()
		fragment.handleRecording(MeetingRecordStatus.RECORDING_STOPPED)
		assertFalse(fragment.isRecordingOn)
	}

	@Test
	fun `test updateRecordingIcon`() {
		launchFragment()
		fragment.isRecordingOn = true
		fragment.updateRecordingIcon()
		assertEquals(fragment.recordingIndicator.visibility, View.VISIBLE)
		fragment.isRecordingOn = false
		fragment.updateRecordingIcon()
		assertEquals(fragment.recordingIndicator.visibility, View.GONE)
	}

	@Test
	fun `test updateAfterMusicOnHold set to true`() {
		launchFragment()
		fragment.meetingUserViewModel.audioConnType = AudioType.VOIP
		fragment.updateAfterMusicOnHold(true)
		assertTrue(fragment.isMusicOnHold)
		assertFalse(fragment.btnMute.isEnabled)
	}

	@Test
	fun `test updateAfterMusicOnHold set to false`() {
		launchFragment()
		fragment.meetingUserViewModel.audioConnType = AudioType.VOIP
		fragment.updateAfterMusicOnHold(false)
		assertTrue(fragment.btnMute.isEnabled)
	}

	@Test
	fun `test handleMeetingEnd`() {
		launchFragment()
		fragment.handleMeetingEnd(MeetingStatus.ENDED)
		assertEquals(fragment.webView.url, "about:blank")
	}

	@Test
	fun `test respondToUserStatus`() {
		launchFragment()
		fragment.respondToUserStatus(UserFlowStatus.END_MEETING_SUCCESS)
		assertEquals(fragment.webView.url, "about:blank")
		fragment.respondToUserStatus(UserFlowStatus.LEAVE_MEETING_SUCCESS)
		assertEquals(fragment.webView.url, "about:blank")
		fragment.respondToUserStatus(UserFlowStatus.DISMISSED)
		assertEquals(fragment.webView.url, "about:blank")
		fragment.meetingUserViewModel.cloundFrontCookies = listOf("domain=pgi.globalmeet.com")
		fragment.isWebViewReady = true
		fragment.contentId = "1234"
		fragment.filePresentatationUrl = "https://file.com"
		fragment.respondToUserStatus(UserFlowStatus.VIDEO_SESSION_SUCCESS)
		assertEquals(fragment.webView.url, "about:blank")
	}

	@Test
	fun `test onContent deleted`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.videoPlayerReady = true
		fragment.isTest = true
		fragment.onContent(Content(
				staticMetadata = StaticMetaData(streamSessionId = "12345",
						documentUrl = "https://file.com",
						fileId = "23456",
						fileObj = FileObj(clientId = "12",
								companyId = "34",
								id = "67",
								media = null,
								name = null,
								metaData = MetaData(contentType = "video",
										createdOn = "23232",
										lastModifiedOn = "132213",
										presentationState = "READY",
										presentationType = "PRESENTATION_TYPE_VIDEO",
										size = "123"))),
				id = "2345",
				type = AppConstants.CONTENT_DELETED,
				dynamicMetaData = DynamicMetaData(
						action = "PLAY_VIDEO",
						playState = "PLAY_VIDEO",
						timeCode = 2,
						eventTime = 0L)))
		assertNull(fragment.contentId)
		assertTrue(!fragment.videoPlayerReady)
	}

	@Test
	fun `test onContent play video`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.videoPlayerReady = true
		fragment.isTest = true
		fragment.onContent(Content(
				staticMetadata = StaticMetaData(streamSessionId = "12345",
						documentUrl = "https://file.com",
						fileId = "23456",
						fileObj = FileObj(clientId = "12",
								companyId = "34",
								id = "67",
								media = null,
								name = null,
								metaData = MetaData(contentType = null,
										createdOn = null,
										lastModifiedOn = null,
										presentationState = "READY",
										presentationType = "PRESENTATION_TYPE_VIDEO",
										size = null))),
				id = "2345",
				type = AppConstants.FILE_PRESENTATION,
				dynamicMetaData = DynamicMetaData(
						action = "PLAY_VIDEO",
						playState = "PLAY_VIDEO",
						timeCode = 2,
						eventTime = 0L)))
		assertEquals(fragment.sessionId, "12345")
//		assertEquals(fragment.webView.url, "javascript:changeVideoPosition('2');")
	}

	@Test
	fun `test onContent play video play on seek`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.videoPlayerReady = true
		fragment.isTest = true
		fragment.onContent(Content(
				staticMetadata = StaticMetaData(streamSessionId = "12345",
						documentUrl = "https://file.com",
						fileId = "23456",
						fileObj = FileObj(clientId = "12",
								companyId = "34",
								id = "67",
								media = null,
								name = null,
								metaData = MetaData(contentType = null,
										createdOn = null,
										lastModifiedOn = null,
										presentationState = "READY",
										presentationType = "PRESENTATION_TYPE_VIDEO",
										size = null))),
				id = "2345",
				type = AppConstants.FILE_PRESENTATION,
				dynamicMetaData = DynamicMetaData(
						action = "SEEK_VIDEO",
						playState = "PLAY_VIDEO",
						playOnSeek = true,
						timeCode = 2,
						eventTime = 0L)))
		assertEquals(fragment.sessionId, "12345")
//		assertEquals(fragment.webView.url, "javascript:changeVideoStatus('play');")
	}


	@Test
	fun `test onContent play video pause on seek`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.videoPlayerReady = true
		fragment.isTest = true
		fragment.onContent(Content(
				staticMetadata = StaticMetaData(streamSessionId = "12345",
						documentUrl = "https://file.com",
						fileId = "23456",
						fileObj = FileObj(clientId = "12",
								companyId = "34",
								id = "67",
								media = null,
								name = null,
								metaData = MetaData(contentType = null,
										createdOn = null,
										lastModifiedOn = null,
										presentationState = "READY",
										presentationType = "PRESENTATION_TYPE_VIDEO",
										size = null))),
				id = "2345",
				type = AppConstants.FILE_PRESENTATION,
				dynamicMetaData = DynamicMetaData(
						action = "SEEK_VIDEO",
						playState = "PLAY_VIDEO",
						playOnSeek = false,
						timeCode = 2,
						eventTime = 0L)))
		assertEquals(fragment.sessionId, "12345")
//		assertEquals(fragment.webView.url, "javascript:changeVideoStatus('play');")
	}

	@Test
	fun `test onContent play video pause`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.videoPlayerReady = true
		fragment.isTest = true
		fragment.onContent(Content(
				staticMetadata = StaticMetaData(streamSessionId = "12345",
						documentUrl = "https://file.com",
						fileId = "23456",
						fileObj = FileObj(clientId = "12",
								companyId = "34",
								id = "67",
								media = Media("sample", "sampleurl"),
								name = null,
								metaData = MetaData(contentType = null,
										createdOn = null,
										lastModifiedOn = null,
										presentationState = "READY",
										presentationType = "PRESENTATION_TYPE_VIDEO",
										size = null))),
				id = "2345",
				type = AppConstants.FILE_PRESENTATION,
				dynamicMetaData = DynamicMetaData(
						action = "PAUSE_VIDEO",
						playState = "PAUSE_VIDEO",
						playOnSeek = false,
						timeCode = 20,
						eventTime = 0L)))
		assertEquals(fragment.sessionId, "12345")
//		assertEquals(fragment.webView.url, "javascript:changeVideoPosition('20');")
	}

	@Test
	fun `test onContent pdf on start`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		fragment.onContent(Content(
				staticMetadata = StaticMetaData(streamSessionId = "12345",
						documentUrl = "https://file.com",
						fileId = "23456",
						fileObj = FileObj(clientId = "12",
								companyId = "34",
								id = "67",
								media = null,
								name = null,
								metaData = MetaData(contentType = null,
										createdOn = null,
										lastModifiedOn = null,
										presentationState = "READY",
										presentationType = "PRESENTATION_TYPE_PDF",
										size = null))),
				id = "2345",
				type = AppConstants.FILE_PRESENTATION,
				dynamicMetaData = DynamicMetaData(newPage = "1")))
		assertEquals(fragment.sessionId, "12345")
//		assertEquals(fragment.webView.url, "javascript:addPdf('2345', 'https://file.com');")
	}

	@Test
	fun `test onContent pdf on page change`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		fragment.currentPageNumber = 2
		fragment.onContent(Content(
				staticMetadata = StaticMetaData(streamSessionId = "12345",
						documentUrl = "https://file.com",
						fileId = "23456",
						fileObj = FileObj(clientId = "12",
								companyId = "34",
								id = "67",
								media = null,
								name = null,
								metaData = MetaData(contentType = null,
										createdOn = null,
										lastModifiedOn = null,
										presentationState = "READY",
										presentationType = "PRESENTATION_TYPE_PDF",
										size = null))),
				id = "2345",
				type = AppConstants.FILE_PRESENTATION,
				dynamicMetaData = DynamicMetaData(newPage = "2")))
		assertEquals(fragment.sessionId, "12345")
//		assertEquals(fragment.webView.url, "javascript:queueRenderPage('2');")
	}

	@Test
	fun callPageFinished() {
		launchFragment()
		fragment.currentPageNumber = 12
		fragment.webView.webViewClient.onPageFinished(fragment.webView, "https://file.com")
	}

	@Test
	fun callPageFinishedNotCalled() {
		launchFragment()
		fragment.currentPageNumber = 0
		fragment.webView.webViewClient.onPageFinished(fragment.webView, "https://file.com")
	}

	@Test
	fun `test onContent screenshare start`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		val content = Content(
				staticMetadata = StaticMetaData(),
				id = "2345",
				type = AppConstants.SCREEN_SHARE,
				dynamicMetaData = DynamicMetaData(type = AppConstants.SCREEN_SHARE, action = AppConstants
						.ACTION_SCREEN_START, screenPresenter = ScreenPresenter("123456")))
		fragment.onContent(content)
		assertEquals(fragment.contentPresenterId, "123456")
	}

	@Test
	fun `test onContent screenshare stop`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		val content = Content(
				staticMetadata = StaticMetaData(),
				id = "2345",
				type = AppConstants.SCREEN_SHARE,
				dynamicMetaData = DynamicMetaData(type = AppConstants.SCREEN_SHARE, action = AppConstants.ACTION_SCREEN_STOP))
		fragment.onContent(content)
		assertNull(fragment.contentPresenterId)
	}

	@Test
	fun `test onContent whiteboard start`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		val content = Content(
				staticMetadata = StaticMetaData(),
				id = "12345",
				type = AppConstants.WHITEBOARD,
				dynamicMetaData = DynamicMetaData(type = AppConstants.WHITEBOARD, action = AppConstants
						.ACTION_SCREEN_START, whiteboardPresenter = WhiteboardPresenter("678910")))
		fragment.onContent(content)
		assertEquals(fragment.contentId, "12345")
	}

	@Test
	fun `test onContent whiteboard stop`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		val content = Content(
				staticMetadata = StaticMetaData(),
				id = "12345",
				type = AppConstants.WHITEBOARD,
				dynamicMetaData = DynamicMetaData(type = AppConstants.WHITEBOARD, action = AppConstants.ACTION_SCREEN_STOP))
		fragment.onContent(content)
		assertNull(fragment.contentId)
	}

	@Test
	fun `test onContent appgoesbackgroundand content not changed() `() {
		launchFragment()
		fragment.contentIdOld = "12345"
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		val content = Content(
				staticMetadata = StaticMetaData(),
				id = "12345",
				type = AppConstants.WHITEBOARD,
				dynamicMetaData = DynamicMetaData(type = AppConstants.WHITEBOARD, action = AppConstants.ACTION_SCREEN_STOP))
		fragment.onContent(content)
		assertNull(fragment.contentId)
	}

	@Test
	fun `test onContent appgoesbackgroundand content  changed() `() {
		launchFragment()
		fragment.contentIdOld = "1234534"
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		val content = Content(
				staticMetadata = StaticMetaData(),
				id = "12345",
				type = AppConstants.WHITEBOARD,
				dynamicMetaData = DynamicMetaData(type = AppConstants.WHITEBOARD, action = AppConstants.ACTION_SCREEN_STOP))
		fragment.onContent(content)
		assertNull(fragment.contentId)
	}

	@Test
	fun `test micdrawable`() {
		launchFragment()
		fragment.setMicDrawables(AudioType.DIAL_OUT)
		fragment.setMicDrawables(AudioType.NO_AUDIO)
		fragment.setMicDrawables(AudioType.DIAL_IN)
		//TODO:: Figure out how to test drawable
//		assertEquals(fragment.btnMute.buttonDrawable.current.id, R.drawable.mic_btn_pstn_selector)
	}

	@Test
	fun `test toggleMic`() {
		launchFragment()
		fragment.updateAfterMusicOnHold(true)
		fragment.toggleMic(true)
		assertEquals(fragment.btnMute.isChecked, true)
		fragment.updateAfterMusicOnHold(false)
		fragment.toggleMic(true)
		assertTrue(fragment.isChecked)
		assertEquals(fragment.btnMute.isChecked, true)
	}

	@Test
	fun `test toggleCamera share`() {
		launchFragment()
		fragment.foxdenSessionActive = true
		fragment.isTest = true
		fragment.btnCamera.isChecked = false
		fragment.toggleCamera(true)
		assertEquals(fragment.btnCamera.isChecked, true)
	}

	@Test
	fun `test toggleCamera share false`() {
		launchFragment()
		fragment.foxdenSessionActive = true
		fragment.isTest = true
		fragment.btnCamera.isChecked = false
		fragment.toggleCamera(false)
		assertEquals(fragment.btnCamera.isChecked, false)
	}

	@Test
	fun `test toggleCamera share same state`() {
		launchFragment()
		fragment.foxdenSessionActive = true
		fragment.isTest = true
		fragment.btnCamera.isChecked = true
		fragment.toggleCamera(true)
		assertEquals(fragment.btnCamera.isChecked, true)
	}

	@Test
	fun `test toggleCamera share false same state`() {
		launchFragment()
		fragment.foxdenSessionActive = true
		fragment.isTest = true
		fragment.btnCamera.isChecked = true
		fragment.toggleCamera(false)
		assertEquals(fragment.btnCamera.isChecked, false)
	}

	@Test
	fun `test toggleCamera stop`() {
		launchFragment()
		fragment.foxdenSessionActive = true
		fragment.isTest = true
		fragment.toggleCamera(false)
		assertEquals(fragment.btnCamera.isChecked, false)
	}

	@Test
	fun `test toggleMicEnableState`() {
		launchFragment()
		fragment.updateAfterMusicOnHold(false)
		fragment.toggleMicEnableState(true)
		assertEquals(fragment.btnMute.isClickable, true)
		assertEquals(fragment.btnMute.isEnabled, true)

		fragment.updateAfterMusicOnHold(false)
		fragment.toggleMicEnableState(false)
		assertEquals(fragment.btnMute.isClickable, true)
		assertEquals(fragment.btnMute.isEnabled, true)

		fragment.updateAfterMusicOnHold(true)
		fragment.toggleMicEnableState(false)
		assertEquals(fragment.btnMute.isClickable, true)
		assertEquals(fragment.btnMute.isEnabled, true)

		fragment.isMusicOnHold =true
		fragment.toggleMicEnableState(false)
		assertEquals(fragment.btnMute.isEnabled, true)

		fragment.meetingUserViewModel.audioConnType = AudioType.DIAL_IN
		fragment.updateAfterMusicOnHold(true)
		fragment.toggleMicEnableState(false)
		assertEquals(fragment.btnMute.isClickable, true)
		assertEquals(fragment.btnMute.isEnabled, true)
	}

	@Test
	fun `test handleInternetConnection`() {
		launchFragment()
		fragment.handleInternetConnection(false)
		assertEquals(fragment.webView.url, "javascript:internetConnectionChanged('false');")
	}

	@Test
	fun `test onConfigurationChanged`() {
		launchFragment()
		val conf = Configuration()
		conf.orientation == Configuration.ORIENTATION_PORTRAIT
		fragment.onConfigurationChanged(conf)
		assertTrue(fragment.isPortrait)
	}

	@Test
	fun `test exitfromFullScreenMode portrait mode`() {
		launchFragment()
		fragment.exitfromFullScreenMode()
		assertFalse(fragment.isFullScreen)
		assertFalse(fragment.isSpotlight)
		assertTrue(fragment.isPortrait)
	}

	@Test
	fun `test exitfromFullScreenMode landscape`() {
		launchFragment()
		fragment.isPortrait = false
		fragment.exitfromFullScreenMode()
		assertFalse(fragment.isFullScreen)
		assertFalse(fragment.isSpotlight)
		assertFalse(fragment.isPortrait)
	}

	@Test
	fun `test onCameraClicked`() {
		launchFragment()
		fragment.onCameraClicked()
		assertEquals(fragment.meetingUserViewModel.isCameraOn, fragment.btnCamera.isChecked)
	}

	@Test
	fun `test onCameraClicked on wifi network`() {
		launchFragment()
		fragment.isGranted = PackageManager.PERMISSION_GRANTED
		mockkConstructor(FeaturesManager::class)
		mockkStatic(InternetConnection::class)
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns false
		fragment.onCameraClicked()
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedWifi(CoreApplication.appContext) } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns true
		fragment.onCameraClicked()
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedWifi(CoreApplication.appContext) } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns false
		fragment.onCameraClicked()
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedWifi(CoreApplication.appContext) } returns false
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns false
		fragment.onCameraClicked()
		assertNotNull(fragment.disableCameraDialog)
		unmockkConstructor(FeaturesManager::class)
		unmockkStatic(InternetConnection::class)
	}

	@Test
	fun `test onCameraClicked on mobile network`() {
		launchFragment()
		fragment.isGranted = PackageManager.PERMISSION_GRANTED
		mockkConstructor(FeaturesManager::class)
		mockkStatic(InternetConnection::class)
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns false
		fragment.onCameraClicked()
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedWifi(CoreApplication.appContext) } returns false
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns false
		fragment.onCameraClicked()
		assertNotNull(fragment.disableCameraDialog)
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedWifi(CoreApplication.appContext) } returns false
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns true
		fragment.onCameraClicked()
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedWifi(CoreApplication.appContext) } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns true
		fragment.onCameraClicked()
		unmockkConstructor(FeaturesManager::class)
		unmockkStatic(InternetConnection::class)
	}

	@Test
	fun `test onMuteMeClicked`() {
		launchFragment()
		val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
		fragment.parentActivity = webMeetingActivity
		fragment.meetingUserViewModel.audioConnType = AudioType.NO_AUDIO
		assertNotNull(fragment.onMuteMeClicked())
		fragment.meetingUserViewModel.audioConnType = AudioType.DIAL_IN
		assertNotNull(fragment.onMuteMeClicked())
		fragment.meetingUserViewModel.audioConnType = AudioType.VOIP
		fragment.onMuteMeClicked()
		assertEquals(fragment.isMuteEnabled, fragment.btnMute.isChecked)
		assertTrue(!fragment.meetingUserViewModel.isMuteBtnEnabled)
	}

	@Test
	fun `test onFullScreenClicked`() {
		launchFragment()
		fragment.onFullScreenClicked()
		assertFalse(fragment.isFullScreen)
		assertFalse(fragment.isSpotlight)
	}

	@Test
	fun `test handleOrientationChange portrait`() {
		launchFragment()
		fragment.isPortrait = true
		fragment.handleOrientationChange()
		assertTrue(!fragment.meetingUserViewModel.screenShareLandScape)
	}

	@Test
	fun `test handleOrientationChange landscape`() {
		launchFragment()
		fragment.isPortrait = false
		fragment.textViewParticipantNameSS.text = "sample"
		fragment.handleOrientationChange()
		assertTrue(fragment.meetingUserViewModel.screenShareLandScape)
	}

	@Test
	fun `test transitionToPortrait when user speaking`() {
		launchFragment()
		fragment.isFullScreen = false
		fragment.talkingState = "Speaking"
		fragment.isPortrait = true
		fragment.handleOrientationChange()
		assertTrue(!fragment.meetingUserViewModel.screenShareFullScreen)
	}

	@Test
	fun `test transitionToPortrait when user speaking with fullscreen`() {
		launchFragment()
		fragment.isFullScreen = true
		fragment.talkingState = "Speaking"
		fragment.isPortrait = true
		fragment.handleOrientationChange()
		assertTrue(fragment.meetingUserViewModel.screenShareFullScreen)
	}

	@Test
	fun `test transitionToPortrait when user not speaking`() {
		launchFragment()
		fragment.isFullScreen = false
		fragment.talkingState = "No one Speaking"
		fragment.isPortrait = true
		fragment.handleOrientationChange()
		assertTrue(!fragment.meetingUserViewModel.screenShareFullScreen)
	}

	@Test
	fun `test transitionToPortrait when user not speaking with fullScreen`() {
		launchFragment()
		fragment.isFullScreen = true
		fragment.talkingState = "No one Speaking"
		fragment.isPortrait = true
		fragment.handleOrientationChange()
		assertTrue(fragment.meetingUserViewModel.screenShareFullScreen)
	}

	@Test
	fun `test transitionToPortrait fullscreen`() {
		launchFragment()
		fragment.isFullScreen = true
		fragment.textViewParticipantNameSS.text = "sample"
		fragment.handleOrientationChange()
		assertTrue(fragment.meetingUserViewModel.screenShareFullScreen)
	}

	@Test
	fun `test JavaScriptInterface accesstoken`() {
		launchFragment()
		mockkStatic(AppAuthUtils::class)
		val authutils = mockkClass(AppAuthUtils::class, relaxed = true)
		every {
			AppAuthUtils.getInstance()
		} returns authutils
		every { authutils.accessToken } returns "ancsjbcjhsbcjsahcs"
		assertEquals(fragment.myJavaScriptInterface?.accessToken(),  "ancsjbcjhsbcjsahcs")
	}

	@Test
	fun `test JavaScriptInterface showActiveTalker`() {
		launchFragment()
		fragment.myJavaScriptInterface?.showActiveTalker()
		assertTrue(fragment.meetingUserViewModel.contentPresentationActive == false)
	}

	@Test
	fun `test JavaScriptInterface hideActiveTalker`() {
		launchFragment()
		fragment.myJavaScriptInterface?.hideActiveTalker()
		assertTrue(fragment.meetingUserViewModel.contentPresentationActive == true)
	}

	@Test
	fun `test JavaScriptInterface foxDenSessionConnected`() {
		launchFragment()
		fragment.myJavaScriptInterface?.foxDenSessionConnected()
		assertTrue(fragment.foxdenSessionActive)
	}

	@Test
	fun `test JavaScriptInterface foxDenSessionDisconnected`() {
		launchFragment()
		fragment.myJavaScriptInterface?.foxDenSessionDisconnected()
		assertTrue(!fragment.meetingUserViewModel.contentPresentationActive)
		assertTrue(!fragment.foxdenSessionActive)
		assertEquals(fragment.webView.url, AppConstants.START_SCREEN_SHARE_AUTH)
	}

	@Test
	fun `test JavaScriptInterface notifyMaxCameraLimitReached`() {
		launchFragment()
		mockWebActivityForParentActivity()
		fragment.myJavaScriptInterface?.notifyMaxCameraLimitReached("6")
		assertNotNull(fragment.contentLowerToast)
	}

	@Test
	fun `test JavaScriptInterface setInFullScreenMode`() {
		launchFragment()
		fragment.myJavaScriptInterface?.setInFullScreenMode("camera")
		assertTrue(fragment.isFullScreen)
		assertTrue(fragment.isSpotlight)
		fragment.myJavaScriptInterface?.setInFullScreenMode("localcamera")
		assertTrue(fragment.isFullScreen)
		assertTrue(fragment.isSpotlight)
		assertTrue(fragment.isLocalStream)
	}

	@Test
	fun `test JavaScriptInterface exitFullScreenMode`() {
		launchFragment()
		fragment.myJavaScriptInterface?.exitFullScreenMode()
		assertFalse(fragment.isFullScreen)
		assertFalse(fragment.isSpotlight)
	}

	@Test
	fun `test JavaScriptInterface setCloudRegion`() {
		launchFragment()
		assertEquals(fragment.myJavaScriptInterface?.setCloudRegion(), "")
	}

	@Test
	fun `test JavaScriptInterface getUrl`() {
		launchFragment()
		assertNull(fragment.myJavaScriptInterface?.getFileUrl())
	}

	@Test
	fun `test JavaScriptInterface getCookies`() {
		launchFragment()
		assertEquals(fragment.myJavaScriptInterface?.getCookies()?.size, 0)
	}

	@Test
	fun `test JavaScriptInterface playerReady`() {
		launchFragment()
		fragment.myJavaScriptInterface?.playerReady()
		assertTrue(fragment.videoPlayerReady)
	}

	@Test
	fun `test JavaScriptInterface getSessionId`() {
		launchFragment()
		fragment.sessionId = "abcd"
		assertEquals(fragment.myJavaScriptInterface?.getSessionId(), "abcd")
	}

	@Test
	fun `test JavaScriptInterface getParticipantId`() {
		launchFragment()
		fragment.participantId = "abcd"
		assertEquals(fragment.myJavaScriptInterface?.getParticipantId(), "abcd")
	}

	@Test
	fun `test JavaScriptInterface getMeetingUrl`() {
		launchFragment()
		assertNull(fragment.myJavaScriptInterface?.getMeetingUrl());
	}

	@Test
	fun `test JavaScriptInterface logErrors`() {
		launchFragment()
		fragment.myJavaScriptInterface?.logErrors("hello")
		fragment.myJavaScriptInterface?.logInfo("hello")
		//TODO:: Comeback to this
//		verify {
//			CoreApplication.mLogger.error(ContentPresentationFragment::class.java.simpleName, LogEvent.FEATURE_SCREENSHARE, LogEventValue.SCREENSHARE,
//					"hello", null, null, false, false)
//		}
	}

	@Test
	fun `test JavaScriptInterface webViewReady`() {
		launchFragment()
		fragment.myJavaScriptInterface?.webViewReady()
		assertTrue(fragment.isWebViewReady)
	}

	@Test
	fun `test JavaScriptInterface webViewReady with sessionId`() {
		launchFragment()
		fragment.sessionId = "1234"
		fragment.myJavaScriptInterface?.webViewReady()
		assertTrue(fragment.isWebViewReady)
	}

	@Test
	fun `test JavaScriptInterface toggleControls`() {
		launchFragment()
		fragment.meetingControls.visibility = View.GONE
		fragment.myJavaScriptInterface?.toggleControls()
		assertEquals(fragment.meetingControls.visibility, View.VISIBLE)
		fragment.meetingControls.visibility = View.VISIBLE
		fragment.myJavaScriptInterface?.toggleControls()
		assertEquals(fragment.meetingControls.visibility, View.GONE)
	}

	@Test
	fun `test JavaScriptInterface toggleNativeZoom`() {
		launchFragment()
		fragment.myJavaScriptInterface?.toggleNativeZoom()
		assertEquals(fragment.currentWebViewScale, fragment.initialScale)

		fragment.initialScale = 120.0f;
		fragment.myJavaScriptInterface?.toggleNativeZoom()
		assertNotEquals(fragment.currentWebViewScale, fragment.initialScale)
	}
	@Test
	fun `test JavaScriptInterface getLoggerEndpoint`() {
		launchFragment()
		assertEquals(fragment.myJavaScriptInterface?.getLoggerEndpoint(), "https://logsvc.globalmeet.com")
	}

	@Test
	fun `test JavaScriptInterface updateStreamCount`() {
		launchFragment()
		every {
			mockMeetingEventViewModel.getUserById("123456")
		} returns User(name = "Test User")
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.meetingEventViewModel = mockMeetingEventViewModel

		fragment.llNameContainer.visibility = View.GONE

//		fragment.myJavaScriptInterface?.updateStreamCount(0,1, 1,0,null, "123456")
//		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
//
//		fragment.llNameContainer.visibility = View.GONE
//		fragment.myJavaScriptInterface?.updateStreamCount(0,2, 1,0,null, null)
//		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
//
//		fragment.llNameContainer.visibility = View.GONE
//		fragment.myJavaScriptInterface?.updateStreamCount(1,0, 0,0,"screen", "123456")
//		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
//
//		fragment.llNameContainer.visibility = View.GONE
//		fragment.myJavaScriptInterface?.updateStreamCount(1,2, 2,0,"screen", "123456")
//		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
//
//		fragment.llNameContainer.visibility = View.GONE
//		fragment.myJavaScriptInterface?.updateStreamCount(1,1, 1,0,"screen", "123456")
//		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
//
//		fragment.llNameContainer.visibility = View.GONE
//		fragment.myJavaScriptInterface?.updateStreamCount(1,2, 2,0,"pdf", "123456")
//		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
//
//		fragment.llNameContainer.visibility = View.GONE
//		fragment.myJavaScriptInterface?.updateStreamCount(1,0, 0,0,"pdf", null)
		fragment.llNameContainer.visibility = View.GONE
		fragment.myJavaScriptInterface?.updateStreamCount(0,1, 1,1, null, "123456")
		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.contentPresentationActive == true
		fragment.llNameContainer.visibility = View.GONE
		fragment.myJavaScriptInterface?.updateStreamCount(0,2, 1,1, null, null)
		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
		fragment.llNameContainer.visibility = View.GONE
		fragment.myJavaScriptInterface?.updateStreamCount(1,0, 0,1, "screen", "123456")
		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
		fragment.llNameContainer.visibility = View.GONE
		fragment.myJavaScriptInterface?.updateStreamCount(1,2, 2,1, "screen", "123456")
		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.contentPresentationActive == true
		fragment.llNameContainer.visibility = View.GONE
		fragment.myJavaScriptInterface?.updateStreamCount(1,1, 1,1, "screen", "123456")
		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.contentPresentationActive == true
		fragment.llNameContainer.visibility = View.GONE
		fragment.myJavaScriptInterface?.updateStreamCount(1,2, 2,2, "pdf", "123456")
		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.contentPresentationActive == true
		fragment.llNameContainer.visibility = View.GONE
		fragment.myJavaScriptInterface?.updateStreamCount(1,0, 0,1, "pdf", null)
		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
		fragment.meetingUserViewModel.contentPresentationActive == true
	}

	@Test
	fun `test JavaScriptInterface showContentStartedToast screen`() {
		launchFragment()
		fragment.contentPresenterId = "123456"
		fragment.vpWebMeeting?.currentItem = 1
		mockWebActivityForParentActivity()
		fragment.myJavaScriptInterface?.showContentStartedToast("test")
		fragment.myJavaScriptInterface?.showContentStartedToast("screen")
		assertNotNull(fragment.contentPresentationNotificationToast)
	}

	private fun mockWebActivityForParentActivity() {
		val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
		fragment.parentActivity = webMeetingActivity
		every {
			fragment.getViewForNotification(fragment.parentActivity)
		} returns fragment.coordinatorLayout
	}

	@Test
	fun `test JavaScriptInterface showContentStartedToast camera`() {
		launchFragment()
		fragment.contentPresenterId = "123456"
		fragment.vpWebMeeting?.currentItem = 1
		mockWebActivityForParentActivity()
		fragment.myJavaScriptInterface?.showContentStartedToast("camera")
		assertNotNull(fragment.contentPresentationNotificationToast)
	}

	@Test
	fun `test JavaScriptInterface showContentStartedToast localstream`() {
		launchFragment()
		fragment.contentPresenterId = "123456"
		fragment.vpWebMeeting?.currentItem = 1
		mockWebActivityForParentActivity()
		fragment.meetingUserViewModel.isCameraOn = true
		fragment.myJavaScriptInterface?.showContentStartedToast("localcamera")
		fragment.meetingUserViewModel.isCameraOn = false
		fragment.myJavaScriptInterface?.showContentStartedToast("localcamera")
		assertNotNull(fragment.contentPresentationNotificationToast)
	}

	@Test
	fun `test JavaScriptInterface showContentStartedToast video`() {
		launchFragment()
		fragment.contentPresenterId = "123456"
		fragment.vpWebMeeting?.currentItem = 1
		mockWebActivityForParentActivity()
		fragment.myJavaScriptInterface?.showContentStartedToast("video")
		assertNotNull(fragment.contentPresentationNotificationToast)
	}

	@Test
	fun `test JavaScriptInterface showContentStartedToast pdf`() {
		launchFragment()
		fragment.contentPresenterId = "123456"
		fragment.vpWebMeeting?.currentItem = 1
		mockWebActivityForParentActivity()
		fragment.myJavaScriptInterface?.showContentStartedToast("pdf")
		assertNotNull(fragment.contentPresentationNotificationToast)
	}

	@Test
	fun `test JavaScriptInterface showContentStartedToast whiteboard`() {
		launchFragment()
		fragment.contentPresenterId = "123456"
		fragment.vpWebMeeting?.currentItem = 1
		mockWebActivityForParentActivity()
		fragment.myJavaScriptInterface?.showContentStartedToast("whiteboard")
		assertNotNull(fragment.contentPresentationNotificationToast)
	}

	@Test
	fun `test JavaScriptInterface updateCameraButtonActiveState`() {
		launchFragment()
		fragment.myJavaScriptInterface?.updateCameraButtonActiveState(true)
		assertTrue(fragment.meetingUserViewModel.isCameraOn)
		fragment.myJavaScriptInterface?.updateCameraButtonActiveState(false)
		assertFalse(fragment.meetingUserViewModel.isCameraOn)
	}

	@Test
	fun `test onVideoActionChange`() {
		launchFragment()
		fragment.onVideoActionChange("start")
		assertEquals(fragment.webView.url, "javascript:changeVideoStatus('start');")
	}

	@Test
	fun `test onVideoSeek`() {
		launchFragment()
		fragment.onVideoSeek("12")
		assertEquals(fragment.webView.url, "javascript:changeVideoPosition('12');")
	}

	@Test
	fun `test customwebview scale changed`() {
		launchFragment()
		fragment.webView.webViewClient.onScaleChanged(fragment.webView, 100.0f, 200.0f)
		assertEquals(fragment.currentWebViewScale, 200.0f)
	}

	@Test
	fun `test foxDenSessionConnected`() {
		launchFragment()
		fragment.myJavaScriptInterface?.foxDenSessionConnected()
	}

	@Test
	fun `test handleInitialActiveTalker`() {
		launchFragment()
		fragment.isPortrait = false
		fragment.handleInitialActiveTalker(true)
		assertEquals(fragment.textViewParticipantNameLandscape.visibility, View.VISIBLE)
		fragment.handleInitialActiveTalker(false)
		assertEquals(fragment.textViewParticipantNameLandscape.visibility, View.GONE)
		fragment.isPortrait = true
		fragment.handleInitialActiveTalker(false)
		assertEquals(fragment.textViewTalkingStateSS.visibility,  View.INVISIBLE)
		fragment.handleInitialActiveTalker(true)
		assertEquals(fragment.textViewTalkingStateSS.visibility,  View.VISIBLE)
		fragment.isFullScreen = true
		fragment.handleInitialActiveTalker(false)
		assertEquals(fragment.textViewNoOneSpokenSS.visibility,  View.VISIBLE)
		fragment.handleInitialActiveTalker(true)
		assertEquals(fragment.textViewNoOneSpokenSS.visibility,  View.INVISIBLE)
	}

	@Test
	fun `test showCameraDisableDialog`() {
		launchFragment()
		fragment.showCameraDisableDialog()
		assertNotNull(fragment.disableCameraDialog)
	}

	@Test
	fun `test onDestroy`() {
		launchFragment()
		fragment.onDestroy()
		assertEquals(fragment.webView.url, "about:blank")
	}

	@Test
	fun `test processMixedContentText`() {
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		assertEquals(fragment.processMixedContentText("screen", 1), "Screenshare & 1 webcam")
		assertEquals(fragment.processMixedContentText("pdf", 1), "File presentation & 1 webcam")
		assertEquals(fragment.processMixedContentText("video", 1), "File presentation & 1 webcam")
		assertEquals(fragment.processMixedContentText("whiteboard", 1), "Whiteboard & 1 webcam")
		assertEquals(fragment.processMixedContentText("", 1), "")
		assertEquals(fragment.processMixedContentText("screen", 3), "Screenshare & 3 webcams")
		assertEquals(fragment.processMixedContentText("pdf", 3), "File presentation & 3 webcams")
		assertEquals(fragment.processMixedContentText("video", 3), "File presentation & 3 webcams")
		assertEquals(fragment.processMixedContentText("whiteboard", 3), "Whiteboard & 3 webcams")
		assertEquals(fragment.processMixedContentText("", 3), "")
	}

	@Test
	fun `test getCameraResolutionInMp`() {
		launchFragment()
		assertNotNull(fragment.getCameraResolution())
	}

	@Test
	fun `Test userWebCamToast`() {
		launchFragment()
		fragment.isCameraShared = true
		fragment.isLocalStream = true
		fragment.notifyUserForWebCameUpdate()
		assertNotNull(fragment.contentPresentationNotificationToast)
		fragment.isCameraShared = true
		fragment.isLocalStream = false
		fragment.notifyUserForWebCameUpdate()
		assertNotNull(fragment.contentPresentationNotificationToast)
		fragment.isCameraShared = false
		fragment.notifyUserForWebCameUpdate()
		assertNotNull(fragment.contentPresentationNotificationToast)
	}

	@Test
	fun `Test CameraClickedNotInSpotlight`() {
		mockkConstructor(FeaturesManager::class)
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		launchFragment()
		fragment.isGranted = 0
		fragment.isCameraShared = true
		fragment.isSpotlight = true
		mockWebActivityForParentActivity()
		fragment.onCameraClicked()
		fragment.notifyUserForWebCameUpdate()
		assertNotNull(fragment.contentPresentationNotificationToast)
		fragment.isSpotlight = false
		fragment.isCameraShared = false
		fragment.onCameraClicked()
		assertNotNull(fragment.contentPresentationNotificationToast)
		unmockkConstructor(FeaturesManager::class)
	}

	@Test
	fun `Test stopSharedCameraForWifiOnlyUsers`() {
		mockkConstructor(FeaturesManager::class)
		mockkStatic(InternetConnection::class)
		launchFragment()

		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedMobile(CoreApplication.appContext) } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns true
		fragment.isCameraShared = true
		fragment.stopSharedCameraForWifiOnlyUsers()
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedMobile(CoreApplication.appContext) } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns false
		fragment.isCameraShared = true
		fragment.stopSharedCameraForWifiOnlyUsers()
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedMobile(CoreApplication.appContext) } returns false
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns false
		fragment.isCameraShared = true
		fragment.stopSharedCameraForWifiOnlyUsers()

		unmockkConstructor(FeaturesManager::class)
		unmockkStatic(InternetConnection::class)
	}

	@Test
	fun `test JavaScriptInterface notifyCameraAdded`() {
		launchFragment()
		fragment.isStreamAdded = true
		fragment.myJavaScriptInterface?.notifyCameraAdded()
		assertTrue(fragment.isStreamAdded)
	}

	@Test
	fun `test exitfromFullScreenMode condition`() {
		launchFragment()
		fragment.exitfromFullScreenMode()
		assertFalse(fragment.isCameraManageFeed)
	}

	@Test
	fun `test exitfromFullScreenMode with true condition`() {
		launchFragment()
		fragment.isCameraManageFeed = true
		fragment.exitfromFullScreenMode()
		assertFalse(fragment.isCameraManageFeed)
	}

	@Test
	fun `test  enable webcam on Wifi`() {
		launchFragment()
		fragment.isStreamAdded = true
		Handler().postDelayed({
			assertEquals(fragment.webView.url, "javascript:showHideWebcam(true);")
		},AppConstants.RETRY_5000_MS.toLong())

	}

	@Test
	fun `test  disable webcam on mobile`() {
		launchFragment()
		fragment.isStreamAdded = true
		Handler().postDelayed({
			assertEquals(fragment.webView.url, "javascript:showHideWebcam(false);")
		},AppConstants.RETRY_5000_MS.toLong())
	}

	@Test
	fun `test handleShareCameraEvents camera shared true`() {
		launchFragment()
		fragment.isCameraShared = true
		fragment.handleShareCameraEvents()
		assertFalse(fragment.btnCamera.isChecked)
		assertFalse(fragment.meetingUserViewModel.isCameraOn)
	}

	@Test
	fun `test handleShareCameraEvents camera shared false`() {
		launchFragment()
		fragment.isCameraShared = false
		fragment.handleShareCameraEvents()
		assertTrue(fragment.btnCamera.isChecked)
		assertTrue(fragment.meetingUserViewModel.isCameraOn)
		assertTrue(fragment.meetingUserViewModel.isCameraConnecting)
	}

	@Test
	fun `test fullScreenMode`() {
		launchFragment()
		fragment.onResume()
		assertFalse(fragment.isFullScreen)
	}

	@Test
	fun transitionToFullScreen() {
		launchFragment()
		fragment.onResume()
		assertFalse(fragment.isFullScreen)
		fragment.isFullScreen = true
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		fragment.meetingUserViewModel = mockMeetingUserViewModel

		fragment.isRecordingOn = true
		every {
			fragment invokeNoArgs TRANSITIONTOFULLSCREEN
		} returns true
		assertTrue(!fragment.meetingUserViewModel.screenShareFullScreen)
		assertEquals(fragment.btnToggleFullScreenWebview.visibility, View.INVISIBLE)
		assertEquals(fragment.meetingControls.visibility, View.VISIBLE)
		assertEquals(fragment.recordingIndicator.visibility, View.VISIBLE)

		fragment.isRecordingOn = false
		every {
			fragment invokeNoArgs TRANSITIONTOFULLSCREEN
		} returns true
		assertEquals(fragment.recordingIndicator.visibility, View.GONE)
	}

	@Test
	fun `test onRequestPermissionResult granted`() {
		mockkConstructor(FeaturesManager::class)
		mockkStatic(InternetConnection::class)
		launchFragment()
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns false
		fragment.onRequestPermissionsResult(AppConstants.PERMISSIONS_REQUEST_CAMERA,arrayOf<String>("android.permission.CAMERA"),intArrayOf(0))
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedWifi(CoreApplication.appContext) } returns true
		fragment.onRequestPermissionsResult(AppConstants.PERMISSIONS_REQUEST_CAMERA,arrayOf<String>("android.permission.CAMERA"),intArrayOf(0))
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedWifi(CoreApplication.appContext) } returns false
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns true
		fragment.onRequestPermissionsResult(AppConstants.PERMISSIONS_REQUEST_CAMERA,arrayOf<String>("android.permission.CAMERA"),intArrayOf(0))
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS.feature)  } returns true
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_WIFI_ONLY_ENABLE.feature)  } returns true
		every { InternetConnection.isConnectedWifi(CoreApplication.appContext) } returns false
		every { anyConstructed<FeaturesManager>().isFeatureEnabled(Features.WEBCAMS_ON_CELLULAR_ENABLE.feature)  } returns false
		fragment.onRequestPermissionsResult(AppConstants.PERMISSIONS_REQUEST_CAMERA,arrayOf<String>("android.permission.CAMERA"),intArrayOf(0))
		assertNotNull(fragment.disableCameraDialog)
		unmockkConstructor(FeaturesManager::class)
		unmockkStatic(InternetConnection::class)
	}

	@Test
	fun `test onRequestPermissionResult deny`() {
		launchFragment()
		mockWebActivityForParentActivity()
		fragment.onRequestPermissionsResult(AppConstants.PERMISSIONS_REQUEST_CAMERA,arrayOf<String>("android.permission.CAMERA"),intArrayOf(-1))
	}

	@Test
	fun `test onRequestPermissionResult deny when view is null`() {
		launchFragment()
		fragment.onRequestPermissionsResult(AppConstants.PERMISSIONS_REQUEST_CAMERA,arrayOf<String>("android.permission.CAMERA"),intArrayOf(-1))
	}

	@Test
	fun `test  paginate view state`() {
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		fragment.pages = 3
		fragment.webcamsPerPage = 3
		fragment.foxdenSessionActive = true
		fragment.myJavaScriptInterface?.updateStreamCount(0, 4, 4, 4, null, "123456")
		assertEquals(fragment.btnPaginateNext.visibility, View.VISIBLE)
		assertEquals(fragment.btnPaginatePrevious.visibility, View.VISIBLE)
		assertEquals(fragment.btnPaginatePrevious.isEnabled, false)
		assertEquals(fragment.btnPaginateNext.isEnabled, true)
		assertEquals(fragment.llNameContainer.visibility, View.VISIBLE)
		assertEquals(fragment.textViewPresenter.text, "4 webcams")
	}

	@Test
	fun `test  paginate next button click`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 5
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.meetingEventViewModel = mockMeetingEventViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		fragment.pages = 3
		fragment.webcamsPerPage = 3
		fragment.foxdenSessionActive = true
		fragment.myJavaScriptInterface?.updateStreamCount(0, 4, 4, 4, null, "123456")
		fragment.btnPaginateNext.performClick()
		assertEquals(fragment.btnPaginatePrevious.isEnabled, true)
		assertEquals(fragment.btnPaginateNext.isEnabled, true)
		Handler().postDelayed({
			assertEquals(fragment.webView.url, "javascript:nextPage();")
		}, AppConstants.RETRY_5000_MS.toLong())

		fragment.foxdenSessionActive = false
		fragment.btnPaginateNext.performClick()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns false
		fragment.myJavaScriptInterface?.updateStreamCount(0, 4, 4, 4, null, "123456")
		fragment.btnPaginateNext.performClick()
		assertEquals(fragment.btnPaginatePrevious.visibility, View.INVISIBLE)
		assertEquals(fragment.btnPaginateNext.visibility,  View.INVISIBLE)
	}

	@Test
	fun `test  paginate previous button click`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 5
		every {
			mockMeetingEventViewModel.currentWebCamPage
		} returns 2
		every {
			mockMeetingUserViewModel.isCameraOn
		} returns true
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.meetingEventViewModel = mockMeetingEventViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		fragment.pages = 5
		fragment.webcamsPerPage = 3
		fragment.meetingEventViewModel.currentWebCamPage = 1
		fragment.foxdenSessionActive = true
		fragment.btnPaginateNext.performClick()
		fragment.myJavaScriptInterface?.updateStreamCount(0, 4, 4, 4, null, "123456")
		fragment.btnPaginatePrevious.performClick()
		assertEquals(fragment.btnPaginatePrevious.isEnabled, true)
		Handler().postDelayed({
			assertEquals(fragment.webView.url, "javascript:prevPage();")
		}, AppConstants.RETRY_5000_MS.toLong())
		fragment.foxdenSessionActive = false
		fragment.btnPaginatePrevious.performClick()
	}

	@Test
	fun `test mixed content with pagination`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 5
		every {
			mockMeetingEventViewModel.initialPage
		} returns 1
		every {
			mockMeetingEventViewModel.currentWebCamPage
		} returns 1
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		fragment.pages = 3
		fragment.webcamsPerPage = 3
		fragment.foxdenSessionActive = true
		fragment.myJavaScriptInterface?.updateStreamCount(1, 4, 4, 4, "screen", "123456")
		assertEquals(fragment.btnPaginatePrevious.isEnabled, false)
		assertEquals(fragment.btnPaginateNext.isEnabled, true)
		assertEquals(fragment.textViewPresenter.text, "Screenshare & 4 webcams")
		fragment.myJavaScriptInterface?.updateStreamCount(1, 4, 4, 4, "pdf", "123456")
		assertEquals(fragment.textViewPresenter.text, "File presentation & 4 webcams")
		fragment.myJavaScriptInterface?.updateStreamCount(1, 4, 4, 4, "video", "123456")
		assertEquals(fragment.textViewPresenter.text, "File presentation & 4 webcams")
		fragment.myJavaScriptInterface?.updateStreamCount(1, 4, 4, 4, "whiteboard", "123456")
		assertEquals(fragment.textViewPresenter.text, "Whiteboard & 4 webcams")
		fragment.myJavaScriptInterface?.updateStreamCount(1, 4, 4, 4, "", "123456")
		assertEquals(fragment.textViewPresenter.text, "")
		fragment.myJavaScriptInterface?.updateStreamCount(0, 2, 2, 2, "", "123456")
		assertEquals(fragment.textViewPresenter.text, "2 Webcams")
	}

	@Test
	fun `test pagination when removed stream at last page`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 6
		every {
			mockMeetingEventViewModel.initialPage
		} returns 10
		every {
			mockMeetingEventViewModel.currentWebCamPage
		} returns 3
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.meetingEventViewModel = mockMeetingEventViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		fragment.pages = 9
		fragment.webcamsPerPage = 3
		fragment.foxdenSessionActive = true
		fragment.meetingEventViewModel.currentWebCamPage = 3
		fragment.meetingEventViewModel.initialPage = 7
		fragment.meetingEventViewModel.lastPage = 5
		fragment.myJavaScriptInterface?.updateStreamCount(1, 6, 6, 7, "", "123456")
	}

	@Test
	fun `test showHideWebCam when other Tab is selected`(){
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.showHideWebCamOnTabSelection(1)
		assertTrue(fragment.showCamsBasedonTab)
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns false
		fragment.showHideWebCamOnTabSelection(1)
		assertTrue(fragment.showCamsBasedonTab)
	}

	@Test
	fun `test showHideWebCam when First Tab is selected`(){
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.showHideWebCamOnTabSelection(0)
		assertFalse(fragment.showCamsBasedonTab)

		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns false
		fragment.showHideWebCamOnTabSelection(0)
		assertFalse(fragment.showCamsBasedonTab)
	}

    @Test
	fun `test onStop`(){
		launchFragment()
		assertNull(fragment.contentIdOld)
	}

	@Test
	fun `test onRenderProcessGone`() {
		launchFragment()
		assertTrue(fragment.webView.webViewClient.onRenderProcessGone(fragment.webView, object : RenderProcessGoneDetail() {
			override fun didCrash(): Boolean {
				return true
			}

			override fun rendererPriorityAtExit(): Int {
				return RETURN_0
			}
		}))
	}

	@Test
	fun `test showHideWebcamOnLifecycleEvent`(){
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.showHideWebCamOnLifeCycleEvent(false)
		assertFalse(fragment.showCamsBasedonTab)

		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns false
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.showHideWebCamOnLifeCycleEvent(false)
		assertFalse(fragment.showCamsBasedonTab)
	}

	@Test
	fun `test showHideWebcamOnLowBandwidth`(){
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns false
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 5
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.meetingEventViewModel = mockMeetingEventViewModel
		fragment.showHideWebCamOnLowBandwidthEvent(false)
		assertFalse(fragment.turnWebCamOff)
		fragment.showHideWebCamOnLowBandwidthEvent(true)
		assertTrue(fragment.turnWebCamOff)
	}

	@Test
	fun `test handleLowBandwidthText`(){
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns false
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 5
		every {
			mockMeetingUserViewModel.isCameraOn
		} returns false
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.meetingEventViewModel = mockMeetingEventViewModel
		fragment.handleLowBandwidthText()
		assertEquals(fragment.textViewPresenter.text,"Low Bandwidth Mode (Webcams hidden)")
	}

	@Test
	fun `test notifyCameraAdded lowbandwidth`() {
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.isStreamAdded = true
		fragment.myJavaScriptInterface?.notifyCameraAdded()
		assertTrue(fragment.isStreamAdded)
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns false
		fragment.isStreamAdded = true
		fragment.myJavaScriptInterface?.notifyCameraAdded()
		assertTrue(fragment.isStreamAdded)
	}

	@Test
	fun `test when activity is not null`(){
		launchFragment()
		val webMeetingActivity = mockk<WebMeetingActivity>(relaxed = true)
		fragment.initializeParentActivity(webMeetingActivity)
	}

	@Test
	fun `test when activity is null`(){
		launchFragment()
		fragment.initializeParentActivity(null)
	}

	@Test
	fun `test lowbandwidth mode`() {
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns false
		every {
			mockMeetingUserViewModel.isCameraOn
		} returns true
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 1
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.meetingEventViewModel = mockMeetingEventViewModel
		fragment.handleLowBandwidthText()
		assertEquals(fragment.textViewPresenter.text,"Low Bandwidth Mode")
	}

	@Test
	fun `test lowbandwidth mode if else`() {
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns false
		every {
			mockMeetingUserViewModel.isCameraOn
		} returns true
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 1
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.handleLowBandwidthText()
		assertEquals(fragment.textViewPresenter.text,"Low Bandwidth Mode")

		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 0
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		assertTrue(fragment.handleLowBandwidthText())
	}

	@Test
	fun `test lowbandwidth mode else`() {
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		every {
			mockMeetingUserViewModel.isCameraOn
		} returns true
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 1
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		assertFalse(fragment.handleLowBandwidthText())
	}

	@Test
	fun `test lowbandwidth mode else conditional check`() {
		launchFragment()
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		every {
			mockMeetingUserViewModel.isCameraOn
		} returns true
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns -1
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		assertFalse(fragment.handleLowBandwidthText())
	}

	@Test
	fun `test  swipe right to left`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 5
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.meetingEventViewModel = mockMeetingEventViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		fragment.pages = 3
		fragment.webcamsPerPage = 3
		fragment.foxdenSessionActive = true
		fragment.isSwipeLeftEnabled = true
		fragment.x1 =20.0f
		fragment.y1 =20.0f
		fragment.x2 =160.0f
		fragment.y2 =100.0f
		fragment.myJavaScriptInterface?.updateStreamCount(0, 4, 4, 4, null, "123456")
		// simulated down event
		var event: MotionEvent = MotionEvent.obtain(1, 0, MotionEvent.ACTION_DOWN, 20.00f, 20.00f, 0)
		fragment.onTouch(fragment.webView, event)
		event = MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 180.00f, 100.00f, 0)
		fragment.onTouch(fragment.webView, event)
		assertEquals(fragment.btnPaginatePrevious.isEnabled, true)
		event = MotionEvent.obtain(1, 1, MotionEvent.ACTION_CANCEL, 180.00f, 100.00f, 0)
		fragment.onTouch(fragment.webView, event)
		Handler().postDelayed({
			assertEquals(fragment.webView.url, "javascript:nextPage();")
		}, AppConstants.RETRY_5000_MS.toLong())
	}

	@Test
	fun `test  swipe left to right`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 5
		every {
			mockMeetingUserViewModel.isCameraOn
		} returns true
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns true
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.meetingEventViewModel = mockMeetingEventViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		fragment.pages = 3
		fragment.webcamsPerPage = 3
		fragment.foxdenSessionActive = true
		fragment.isSwipeRightEnabled = true
		fragment.x1 =360.0f
		fragment.y1 =20.0f
		fragment.x2 =160.0f
		fragment.y2 =100.0f
		fragment.myJavaScriptInterface?.updateStreamCount(0, 4, 4, 4, null, "123456")
		// simulated down event
		val event = MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 180.00f, 100.00f, 0)
		fragment.onTouch(fragment.webView, event)
		assertEquals(fragment.btnPaginatePrevious.isEnabled, true)
		Handler().postDelayed({
			assertEquals(fragment.webView.url, "javascript:nextPage();")
		}, AppConstants.RETRY_5000_MS.toLong())
	}


	@Test
	fun `test  swipe left to right webcam 2`() {
		launchFragment()
		every { mockMeetingUserViewModel.makeSessionCallToCloudFront(any()) } just runs
		every {
			mockMeetingEventViewModel.totalWebCam
		} returns 2
		every {
			mockMeetingUserViewModel.isCameraOn
		} returns true
		every {
			mockMeetingUserViewModel.turnWebcamOff
		} returns false
		fragment.meetingUserViewModel = mockMeetingUserViewModel
		fragment.meetingEventViewModel = mockMeetingEventViewModel
		fragment.isWebViewReady = true
		fragment.isTest = true
		fragment.pages = 3
		fragment.webcamsPerPage = 3
		fragment.foxdenSessionActive = true
		fragment.isSwipeRightEnabled = true
		fragment.x1 =360.0f
		fragment.y1 =20.0f
		fragment.x2 =160.0f
		fragment.y2 =100.0f
		fragment.myJavaScriptInterface?.updateStreamCount(0, 4, 4, 4, null, "123456")
		// simulated down event
		val event = MotionEvent.obtain(1, 1, MotionEvent.ACTION_UP, 180.00f, 100.00f, 0)
		fragment.onTouch(fragment.webView, event)
		assertEquals(fragment.isSwipeRightEnabled, false)
	}

	@Test
	fun `test setAudioProgressState when Connected`(){
		launchFragment()
		fragment.setAudioProgressState(AudioStatus.CONNECTED)
		assertTrue(fragment.progressMicButton.visibility == View.GONE)

		fragment.meetingUserViewModel.audioConnType = AudioType.DIAL_IN
		fragment.setAudioProgressState(AudioStatus.CONNECTED)
		assertTrue(fragment.progressMicButton.visibility == View.GONE)
	}

	@Test
	fun `test setAudioProgressState when connecting`(){
		launchFragment()
		fragment.meetingUserViewModel.audioConnType = AudioType.VOIP
		fragment.setAudioProgressState(AudioStatus.CONNECTING)
		assertTrue(fragment.progressMicButton.visibility == View.VISIBLE)

		fragment.meetingUserViewModel.audioConnType = AudioType.DIAL_OUT
		fragment.setAudioProgressState(AudioStatus.CONNECTING)
		assertTrue(fragment.progressMicButton.visibility == View.VISIBLE)

		fragment.meetingUserViewModel.audioConnType = AudioType.DIAL_IN
		fragment.setAudioProgressState(AudioStatus.CONNECTING)
		assertTrue(fragment.progressMicButton.visibility == View.GONE)
	}

	@Test
	fun `test setAudioProgressState when disconnected or not`(){
		launchFragment()
		fragment.setAudioProgressState(AudioStatus.DISCONNECTED)
		assertTrue(fragment.progressMicButton.visibility == View.GONE)
		fragment.setAudioProgressState(AudioStatus.NOT_CONNECTED)
		assertTrue(fragment.progressMicButton.visibility == View.GONE)
	}

	@Test
	fun `test updateContentID `(){
		launchFragment()
		fragment.updateContentID()
		assertNull(fragment.contentIdOld)
	}
}
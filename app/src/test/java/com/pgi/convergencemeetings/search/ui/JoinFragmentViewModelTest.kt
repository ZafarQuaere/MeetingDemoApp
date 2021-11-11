package com.pgi.convergencemeetings.search.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.constants.AppConstants
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.search.di.searchGuestTestModule
import com.pgi.convergencemeetings.search.di.searchTestModule
import com.pgi.network.models.SearchResult
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, application = TestApplication::class)
class JoinFragmentViewModelTest: KoinTest {

  @get:Rule
  val taskExecutorRule = InstantTaskExecutorRule()

  private val joinFragmentViewModel: JoinFragmentViewModel by inject()
  private lateinit var fragment: JoinMeetingFragment
  val mockSeacrhResult = SearchResult(
			123213213L,
			313123,
			31321321,
			"312312312",
			"Test",
			31231,
			false,
			312312,
			"globalmeet5",
			"https://pgi.globalmeet.com/test",
			"121233",
			"3423423423423",
			"1233",
			"HOST",
			"Test",
			"User",
			"https://pgi.globalmeet.com/test",
			12123,
			true,
			0,
			null
																																							 )

  private fun hostStartModule() {
    stopKoin()
    startKoin {
      modules(searchTestModule)
    }
  }

  private fun guesttartModule() {
    stopKoin()
    startKoin {
      modules(searchGuestTestModule)
    }
  }

  @After
  fun `tear down`() {
    stopKoin()
  }

  @Test
  fun `test search results`() {
    hostStartModule()
    launchFragment()
    fragment.notifyAdapterOnSearchUpdate(listOf(mockSeacrhResult))
    joinFragmentViewModel.getSearchResults("test")
    verify {
      joinFragmentViewModel.gmElasticSearchRepository.search("test")
    }
  }

  @Test
  fun `test guest search results`() {
    guesttartModule()
    joinFragmentViewModel.getSearchResults("test")
    verify(exactly = 0) {
      joinFragmentViewModel.gmElasticSearchRepository.search("test")
    }
  }

  @Test
  fun `test suggest results`() {
    hostStartModule()
    joinFragmentViewModel.getSuggestResults("test", true)
    verify {
      joinFragmentViewModel.gmElasticSearchRepository.suggest("test")
    }
  }

  @Test
  fun `test suggest results with first and last name`() {
    hostStartModule()
    joinFragmentViewModel.getSuggestResults("test user", true)
    verify {
      joinFragmentViewModel.gmElasticSearchRepository.suggest("test+user")
    }
  }

  @Test
  fun `test suggest results with first and no last name with space`() {
    hostStartModule()
    joinFragmentViewModel.getSuggestResults("test ", true)
    verify {
      joinFragmentViewModel.gmElasticSearchRepository.suggest("test")
    }
  }

  @Test
  fun `test suggest results with url`() {
    hostStartModule()
    joinFragmentViewModel.getSuggestResults("https://pgi.globalmeet.com/test", true)
    verify {
      joinFragmentViewModel.gmElasticSearchRepository.getMeetingRoomInfoFromFurl("https://pgi.globalmeet.com/test")
    }
  }

  @Test
  fun `test suggest results with url guest`() {
    guesttartModule()
    joinFragmentViewModel.getSuggestResults("https://test.testdoamin.com/test", true)
    verify {
      joinFragmentViewModel.gmElasticSearchRepository.getMeetingRoomInfoFromFurl("https://test.testdoamin.com/test")
    }
  }

  @Test
  fun `test long press`() {
    hostStartModule()
    launchFragment()
    fragment.notifyAdapterOnSearchUpdate(listOf(mockSeacrhResult))
    fragment.meetingRoomsAdapter?.ViewHolder(fragment.mRecentMeetingsRecyclerView)?.mMeetingUrl?.performLongClick()
    Assertions.assertThat(CommonUtils.copyRoomURL(CommonUtils.getCtx(), listOf(mockSeacrhResult).get(0).getFurl(), AppConstants.COPY_URL_UNIVERSAL)
    )
  }
  private fun launchFragment(onInstantiated: (JoinMeetingFragment) -> Unit = {}):
      FragmentScenario<JoinMeetingFragment> {
    return FragmentScenario.launchInContainer(JoinMeetingFragment::class.java, null, R.style
        .AppTheme, object : FragmentFactory() {
      override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val fragment = super.instantiate(classLoader, className) as JoinMeetingFragment
        this@JoinFragmentViewModelTest.fragment = fragment
        onInstantiated(fragment)
        return fragment
      }
    })
  }
}
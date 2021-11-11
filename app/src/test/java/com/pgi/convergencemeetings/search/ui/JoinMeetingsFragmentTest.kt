package com.pgi.convergencemeetings.search.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.action.ViewActions
import com.kevinmost.junit_retry_rule.RetryRule
import com.pgi.convergence.agenda.R
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.base.ui.BaseActivity
import com.pgi.convergencemeetings.greendao.ApplicationDao
import com.pgi.convergencemeetings.search.di.joinMeetingFragmentModule
import com.pgi.convergencemeetings.utils.AppAuthUtils
import com.pgi.logging.Logger
import com.pgi.network.models.SearchResult
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.mockito.Mock
import org.robolectric.annotation.Config


@Config(application = TestApplication::class)
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class JoinMeetingsFragmentTest: KoinTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val retry: RetryRule = RetryRule()

    val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    private var fragment: JoinMeetingFragment? = null
    private var scenario: FragmentScenario<JoinMeetingFragment>? = null
    private var mRecentMeetingsFragment: JoinMeetingFragment? = null

    @Mock
    var looger: Logger? = null
    @Mock
    private val appAuth: AppAuthUtils? = null

    private fun launchFragment(onInstantiated: (JoinMeetingFragment) -> Unit = {}):
            FragmentScenario<JoinMeetingFragment> {
        return FragmentScenario.launchInContainer(JoinMeetingFragment::class.java, null, R.style
                .AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as JoinMeetingFragment
                this@JoinMeetingsFragmentTest.fragment = fragment
                onInstantiated(fragment)
                return fragment
            }
        })
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        mRecentMeetingsFragment = JoinMeetingFragment()
        Dispatchers.setMain(dispatcher)
        ViewActions.closeSoftKeyboard()
    }

    @After
    fun after() {
        stopKoin()
        scenario?.moveToState(Lifecycle.State.DESTROYED)
        fragment = null
    }

    @Test
    @Throws(Exception::class)
    fun shouldNotBeNull() {
        appAuth?.setLogger(looger)
        Assert.assertNotNull(mRecentMeetingsFragment)
    }

    @Test
    fun `test onRecentMeetingClick for SearchResult`() {

        val mockContext: Context = mockkClass(Context::class)
        val oldDao = ApplicationDao.get(mockContext)
        oldDao.invalidate()
        ApplicationDao.setInstance(null)

        val context: Context = ApplicationProvider.getApplicationContext()
        val applicationDao = ApplicationDao.get(context)

        val searchResult = SearchResult()
        searchResult.hubConfId = 12345
        searchResult.conferenceId = 1234
        searchResult.useHtml5 = true
        searchResult.furl = "https://pgi.globalmeet.com/gauravsingh"

        startKoinApp()
        scenario = launchFragment()
        val spy = spyk(fragment!!)
        val activity = BaseActivity()
        every { spy.activity } returns activity
        spy.onRecentMeetingClick(searchResult)
    }

    private fun startKoinApp() {
        startKoin {
            modules(joinMeetingFragmentModule)
        }
    }

}
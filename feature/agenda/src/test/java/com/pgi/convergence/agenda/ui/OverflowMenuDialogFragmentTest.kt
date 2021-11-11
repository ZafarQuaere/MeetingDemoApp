package com.pgi.convergence.agenda.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.filters.LargeTest
import com.pgi.convergence.agenda.R
import com.pgi.convergence.agenda.di.meetingsTestModuleWithNoMsalUser
import io.jsonwebtoken.lang.Assert
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest


@ObsoleteCoroutinesApi
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
class OverflowMenuDialogFragmentTest  : KoinTest {

    private lateinit var fragment: OverflowMenuDialogFragment
        private set

    private fun launchFragment(onInstantiated: (OverflowMenuDialogFragment) -> Unit = {}):
            FragmentScenario<OverflowMenuDialogFragment> {
        return FragmentScenario.launchInContainer(OverflowMenuDialogFragment::class.java, null, R.style
                .AppTheme, object : FragmentFactory() {
            override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                val fragment = super.instantiate(classLoader, className) as OverflowMenuDialogFragment
                this@OverflowMenuDialogFragmentTest.fragment = fragment
                onInstantiated(fragment)
                return fragment
            }
        })
    }

    @Before
    fun setUp() {
        startKoinApp()
        launchFragment()
        fragment.viewModel.firstName = "Nnenna"
        fragment.viewModel.lastName = "Iheke"
        fragment.viewModel.joinUrlData.value = "http://pgi.com/nnenna"
        fragment.viewModel.profileImage = "https://id-data.globalmeet.com/r/profile-images/100x100/1b3e9d5c-3baa-4738-ae62-eaf5ce6bdcf3"
        fragment.viewModel.initials = "NI"
        fragment.viewModel.meetingRoomName = "My Meeting Room"

    }

    @After
    fun after() {
        stopKoin()
    }


    @Test
    fun getBinding() {
        Assert.notNull(fragment.binding)


    }

    @Test
    fun getViewModel() {
        Assert.notNull(fragment.viewModel)


    }
    @Test
    fun getJoinUrl() {
        Assert.notNull(fragment.viewModel.joinUrl)
        Assert.isTrue(fragment.viewModel.joinUrlData.value == "http://pgi.com/nnenna")
    }


    @Test
    fun populateDefaultRoomData() {
        Assert.notNull(fragment.viewModel.meetingRoomName)
        Assert.notNull(fragment.viewModel.initials)
    }

    private fun startKoinApp() {
        startKoin {
            modules(meetingsTestModuleWithNoMsalUser)
        }
    }
}
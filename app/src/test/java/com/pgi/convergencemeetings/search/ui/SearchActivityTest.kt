package com.pgi.convergencemeetings.search.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.utils.CommonUtils
import com.pgi.convergencemeetings.R
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.base.di.baseTestModule
import com.pgi.convergencemeetings.search.di.searchTestModule
import com.pgi.convergencemeetings.utils.checkHasNoText
import com.pgi.convergencemeetings.utils.click
import com.pgi.convergencemeetings.utils.performTypeText
import com.pgi.convergencemeetings.utils.performViewAction
import com.squareup.picasso.Picasso
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkStatic
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class SearchActivityTest: KoinTest {
  @get:Rule
  val taskExecutorRule = InstantTaskExecutorRule()
  @Before
  fun setupKoin() {
    stopKoin()
    startKoin {
      modules(listOf(baseTestModule, searchTestModule))
    }
    val picasso = mockkClass(Picasso::class, relaxed = true)
    mockkStatic(Picasso::class)
    coEvery { Picasso.get() } returns picasso
    mockkStatic(CommonUtils::class)

    every {
      CommonUtils.hideKeyboard(any())
    } returns true
  }

  @After
  fun after() {
    stopKoin()
  }


  @Test
  fun `test on Search close btn clear search text`() {
    val scenario =
      ActivityScenario.launch(SearchActivity::class.java)
    scenario.onActivity {
      Assert.assertNotNull(it)
    }
    R.id.searchEditText.performTypeText("test")
    R.id.search_close_btn.performViewAction(click())
    R.id.searchEditText.checkHasNoText("test")
  }
}
//package com.pgi.convergencemeetings.base.ui
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.test.espresso.action.ViewActions
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.pgi.convergencemeetings.application.TestApplication
//import com.pgi.convergencemeetings.base.di.baseTestModule
//import com.pgi.convergencemeetings.base.di.homeTestModuleWithNoMsalUser
//import com.pgi.convergencemeetings.base.di.meetingsTestModuleWithNoMsalUser
//import com.squareup.picasso.Picasso
//import io.mockk.every
//import io.mockk.mockkClass
//import io.mockk.mockkStatic
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.test.TestCoroutineDispatcher
//import kotlinx.coroutines.test.setMain
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.koin.core.context.startKoin
//import org.koin.core.context.stopKoin
//import org.koin.test.KoinTest
//import org.robolectric.annotation.Config
//
//@RunWith(AndroidJUnit4::class)
//@Config(application = TestApplication::class)
//class AppBaseLayoutActivityTest: KoinTest {
//
//  @get:Rule
//  val taskExecutorRule = InstantTaskExecutorRule()
//
//  val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
//
//  fun setupKoin() {
//    startKoin {
//      modules(listOf(baseTestModule, homeTestModuleWithNoMsalUser, meetingsTestModuleWithNoMsalUser))
//    }
//    val picasso = mockkClass(Picasso::class, relaxed = true)
//    mockkStatic(Picasso::class)
//    every { Picasso.get() } returns picasso
//  }
//
//  @Before
//  fun `set up`() {
//    setupKoin()
//    Dispatchers.setMain(dispatcher)
//    ViewActions.closeSoftKeyboard()
//  }
//
//  @After
//  fun after() {
//    stopKoin()
//  }
//
//  @Test
//  fun `test Activity is not null`() {
////    val scenario =
////      ActivityScenario.launch(AppBaseLayoutActivity::class.java)
////    scenario.onActivity {
////      Assert.assertNotNull(it)
////    }
//  }
//}
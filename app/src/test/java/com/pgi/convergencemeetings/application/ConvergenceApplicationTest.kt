package com.pgi.convergencemeetings.application

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergencemeetings.BuildConfig
import com.pgi.convergencemeetings.base.ConvergenceApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ConvergenceApplicationTest {

  private lateinit var convergenceApplication: ConvergenceApplication

  @Before
  fun setUp() {
    convergenceApplication = ApplicationProvider.getApplicationContext()
  }

  @ObsoleteCoroutinesApi
  @ExperimentalCoroutinesApi
  @Test
  fun parseSupportEmail() {
    convergenceApplication.parseSupportEmail()
  }
}
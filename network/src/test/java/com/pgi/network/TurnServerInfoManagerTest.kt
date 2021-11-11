package com.pgi.network

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.MockKAnnotations
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class TurnServerInfoManagerTest: KoinTest {

  private val turnServerInfoManager: TurnServerInfoManager = TurnServerInfoManager()

  @Before
  fun setUp() {
    MockKAnnotations.init(this, relaxed = true)
    startKoin{
    }
  }

  @After
  fun `tear down`() {
    stopKoin()
  }

  @Test
  fun getTurnServerInfo() {
    TurnServerInfoManager.turnURL = "https://test"
    val x = turnServerInfoManager.getTurnServerInfo("https://test")
    Assert.assertNotNull(x)
  }

  @Test
  fun testGetTurnRestInfo() {
    TurnServerInfoManager.turnRestSuccess = true
    TurnServerInfoManager.turnURL = "https://test"
    val x = turnServerInfoManager.getTurnRestInfo()
    Assert.assertNotNull(x)
  }

  @Test
  fun testStartStopCredentialChecker() {
    TurnServerInfoManager.turnRestSuccess = true
    TurnServerInfoManager.turnURL = "https://test"
    val x = turnServerInfoManager.getTurnRestInfo()
    Assert.assertNotNull(x)
    turnServerInfoManager.startCredentialExpiryChecker()
    Assert.assertNotNull(turnServerInfoManager.credentialExpiryCheckerTimer)
    turnServerInfoManager.stopCredentialExpiryChecker()
    Assert.assertNotNull(turnServerInfoManager.credentialExpiryCheckerTimer)
    turnServerInfoManager.credentialExpiryCheckerTimer = null
    turnServerInfoManager.stopCredentialExpiryChecker()
    Assert.assertNull(turnServerInfoManager.credentialExpiryCheckerTimer)
  }

  @Test
  fun testGetTurnRestInfoNullUrl() {
    TurnServerInfoManager.turnRestSuccess = true
    TurnServerInfoManager.turnURL = null
    TurnServerInfoManager.turnServerInfo = null
    val x = turnServerInfoManager.getTurnRestInfo()
    Assert.assertNull(TurnServerInfoManager.turnServerInfo)
  }
}
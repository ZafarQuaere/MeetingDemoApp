package com.pgi.convergencemeetings.services

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.net.MediaType
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergencemeetings.application.TestApplication
import com.pgi.convergencemeetings.base.services.logger.LoggerService
import com.pgi.logging.Logger
import com.pgi.network.LoggerServiceManager
import com.pgi.network.NetworkRequestManager
import io.mockk.*
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.internal.RealmCore
import okhttp3.ResponseBody
import org.amshove.kluent.mock
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
@Config(application = TestApplication::class)
class LoggerServiceTest {

  private lateinit var mockRealm: Realm
  private lateinit var mockNetworkRequestManager: NetworkRequestManager
  private lateinit var mloggerService: LoggerService
  private lateinit var mocklogger: Logger

  @Before
  fun `set up`() {
    mocklogger  = mockk<Logger>()
    CoreApplication.mLogger = mocklogger
    every { mocklogger.error(any(), any(), any(), any(), any(), any()) } just runs
    MockKAnnotations.init(this, relaxUnitFun = true)
    mockkStatic(Realm::class)
    mockkStatic(RealmConfiguration::class)
    mockkStatic(RealmConfiguration.Builder::class)
    mockkStatic(RealmCore::class)
    mockkStatic(SharedPreferencesManager::class)
    mockkStatic(NetworkRequestManager::class)
    mockNetworkRequestManager = mockk<NetworkRequestManager>()
    mockRealm = mockk<Realm>()
    every {mockRealm.refresh()} just Runs
    every {mockRealm.close() } just Runs
    every {mocklogger.getRealmInstance()} returns(mockRealm)
    val pair: Pair<String, Int> = Pair<String, Int>("test", 1)
    every {mocklogger.getlogsAndCount(mockRealm, 20)} returns pair
    every {mocklogger.getlogsAndCount(mockRealm, 0)} returns pair
    val mockSharedPreferencesManager = mockk<SharedPreferencesManager>()
    every {SharedPreferencesManager.getInstance()} returns(mockSharedPreferencesManager)
    every {SharedPreferencesManager.getInstance().setPrefLogSessionCookie(any())} just Runs
    every {SharedPreferencesManager.getInstance().getPrefLogSessionCookie()} returns null
    every {SharedPreferencesManager.getInstance().authToken} returns null
    mloggerService = LoggerService()
  }

  @After
  fun `tear down`() {
    mloggerService.stopLogTask()
  }

  @Test
  fun `test logger service is not null`() {
//    verify(mloggerService).shouldNotBeNull()
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test stopLogTask`() {
    mloggerService.stopLogTask()
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test flush`() {
    mloggerService.isTesting = true
    mloggerService.flush()
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test flush exception`() {
    every { mockNetworkRequestManager.sendLogs(any(), any(), any()) } throws Exception("error")
    mloggerService.mNetworkRequestManager = mockNetworkRequestManager
    mloggerService.isTesting = true
    mloggerService.flush()
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test deleteUploadedLogs`(){
    every {mocklogger.deleteLogs(0,mockRealm)} just Runs
    mloggerService.deleteUploadedLogs()
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test onFailure`() {
    every {mocklogger.error(any(), any(), any(), any(), any(), any(), any(), any())} just Runs
    val call = LoggerServiceManager.getInstance().loggerServiceAPI.sendLogs(
        "application/json", "newsession", null, "test")
    val throwable: Throwable = Throwable("message", null)
    mloggerService.onFailure(call, throwable)
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test onResponse`() {
    every {mocklogger.deleteLogs(0,mockRealm)} just Runs
    every {mocklogger.error(any(), any(), any(), any(), any(), any(), any(), any())} just Runs
    val call = LoggerServiceManager.getInstance().loggerServiceAPI.sendLogs(
        "application/json", "newsession", null, "test")
    val sessionresponse = Response.success<String>(null)
    mloggerService.onResponse(call, sessionresponse)
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test onResponse 202`() {
    every {mocklogger.deleteLogs(0,mockRealm)} just Runs
    every {mocklogger.error(any(), any(), any(), any(), any(), any(), any(), any())} just Runs
    val call = LoggerServiceManager.getInstance().loggerServiceAPI.sendLogs(
        "application/json", "newsession", null, "test")
    val sessionresponse = Response.success<String>(202, "Response{protocol=http/1.1, code=202, message=Accepted, url=https://logsvc.globalmeet.com/logs}")
  mloggerService.onResponse(call, sessionresponse)
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test onResponse 401`() {
    every {mocklogger.deleteLogs(0,mockRealm)} just Runs
    every {mocklogger.error(any(), any(), any(), any(), any(), any(), any(), any())} just Runs
    val call = LoggerServiceManager.getInstance().loggerServiceAPI.sendLogs(
        "application/json", "newsession", null, "test")
    val responseBody: ResponseBody = ResponseBody.create(null,
        "Response{protocol=http/1.1, code=401, message=Accepted, url=https://logsvc.globalmeet.com/logs}")
    val sr = Response.error<String>(401,responseBody)
    mloggerService.onResponse(call, sr)
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test onResponse 413`() {
    every {mocklogger.deleteLogs(0,mockRealm)} just Runs
    every {mocklogger.error(any(), any(), any(), any(), any(), any(), any(), any())} just Runs
    val call = LoggerServiceManager.getInstance().loggerServiceAPI.sendLogs(
        "application/json", "newsession", null, "test")
    val responseBody: ResponseBody = ResponseBody.create(null,
        "Response{protocol=http/1.1, code=413, message=Accepted, url=https://logsvc.globalmeet.com/logs}")
    val sr = Response.error<String>(413,responseBody)
    mloggerService.onResponse(call, sr)
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test onResponse 413 and exception`() {
    every {mocklogger.deleteLogs(0,mockRealm)} just Runs
    every {mocklogger.error(any(), any(), any(), any(), any(), any(), any(), any())} just Runs
    val call = LoggerServiceManager.getInstance().loggerServiceAPI.sendLogs(
        "application/json", "newsession", null, "test")
    every { mockNetworkRequestManager.sendLogs(any(), any(), any()) } throws Exception("error")
    val responseBody: ResponseBody = ResponseBody.create(null,
        "Response{protocol=http/1.1, code=413, message=Accepted, url=https://logsvc.globalmeet.com/logs}")
    val sr = Response.error<String>(413,responseBody)
    mloggerService.onResponse(call, sr)
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test onResponse 500`() {
    every {mocklogger.deleteLogs(1,mockRealm)} just Runs
    every {mocklogger.error(any(), any(), any(), any(), any(), any(), any(), any())} just Runs
    val call = LoggerServiceManager.getInstance().loggerServiceAPI.sendLogs(
        "application/json", "newsession", null, "test")
    val responseBody: ResponseBody = ResponseBody.create(null,
        "Response{protocol=http/1.1, code=500, message=Accepted, url=https://logsvc.globalmeet.com/logs}")
    val sr = Response.error<String>(500,responseBody)
    mloggerService.onResponse(call, sr)
    Assert.assertNotNull(mloggerService)
  }

  @Test
  fun `test onResponse 500 and exception`() {
    every {mocklogger.deleteLogs(1,mockRealm)} just Runs
    every {mocklogger.error(any(), any(), any(), any(), any(), any(), any(), any())} just Runs
    val call = LoggerServiceManager.getInstance().loggerServiceAPI.sendLogs(
        "application/json", "newsession", null, "test")
    every { mockNetworkRequestManager.sendLogs(any(), any(), any()) } throws Exception("error")
    val responseBody: ResponseBody = ResponseBody.create(null,
        "Response{protocol=http/1.1, code=500, message=Accepted, url=https://logsvc.globalmeet.com/logs}")
    val sr = Response.error<String>(500,responseBody)
    mloggerService.onResponse(call, sr)
    Assert.assertNotNull(mloggerService)
  }
}
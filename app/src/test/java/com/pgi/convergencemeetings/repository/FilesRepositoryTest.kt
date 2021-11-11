package com.pgi.convergencemeetings.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.whenever
import com.pgi.convergencemeetings.RobolectricTest
import com.pgi.convergencemeetings.meeting.gm5.data.repository.files.FileApi
import com.pgi.convergencemeetings.meeting.gm5.data.repository.files.FilesRespository
import io.reactivex.Observable
import okhttp3.Headers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import retrofit2.Response

@PrepareForTest(FileApi::class)
class FilesRepositoryTest: RobolectricTest() {

    @get:Rule
    val rule: PowerMockRule = PowerMockRule()
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var fileApi: FileApi

    @InjectMocks
    private lateinit var filesRespository: FilesRespository

    private val headers = Headers.of("Set-Cookie", "CloudFront-Key-pair-Id=", "Set-Cookie", "CloudFront-Policy=", "Set-Cookie", "CloudFront-Signature=")

    private val sessionresponse = Response.success<Void>(null)

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        sessionresponse.headers().newBuilder().addAll(headers)
        whenever(fileApi.createSession(Mockito.anyString())).thenReturn(Observable.just(sessionresponse))
        filesRespository = FilesRespository.instance
        filesRespository.fileService = fileApi
    }

    @Test
    fun testCreateSession() {
       val testObserver =  filesRespository.createSession("hwbfhc-sdcsdcds-vsvsvsdvds-svdsvds").test()
        testObserver.assertNoErrors()
    }
}
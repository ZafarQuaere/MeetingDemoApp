package com.pgi.convergencemeetings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.greendao.ApplicationDao
import com.pgi.convergencemeetings.models.DaoSession
import com.pgi.convergencemeetings.models.UUID
import com.pgi.convergencemeetings.models.UUIDDao
import com.pgi.convergencemeetings.utils.UUIDGenerator
import org.junit.*
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.rule.PowerMockRule
import java.util.*


@PrepareForTest(UUIDGenerator::class, ApplicationDao::class, java.util.UUID::class, UUID::class)
class UUIDGeneratorTest(): RobolectricTest() {

    @get:Rule
    var rule = PowerMockRule()
    private var uuidDao: UUIDDao? = null
    private var context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun before() {
        CoreApplication.appContext = context;
        CoreApplication.mLogger = TestLogger()
        Mockito.mock(DaoSession::class.java);
        PowerMockito.mockStatic(ApplicationDao::class.java);
        val applicationDao = PowerMockito.mock(ApplicationDao::class.java);
        PowerMockito.`when`(ApplicationDao.get(context)).thenReturn(applicationDao);
        uuidDao = PowerMockito.mock(UUIDDao::class.java)
        Mockito.`when`(ApplicationDao.get(context).uuid).thenReturn(uuidDao);
    }

    @Ignore
    @Test
    fun `test it generates random UUID if no UUID is cached`() {
        PowerMockito.`when`<List<UUID>>(uuidDao?.loadAll()).thenReturn(emptyList());
        val id = "493410b3-dd0b-4b78-97bf-289f50f6e74f"
        val uuid = java.util.UUID.fromString(id)
        PowerMockito.`when`(java.util.UUID.randomUUID()).thenReturn(uuid);
        Assert.assertEquals(UUIDGenerator.uUID, id);
    }

    @Ignore
    @Test
    fun `test it returns UUID from cache`() {
        PowerMockito.`when`<List<UUID>>(uuidDao?.loadAll()).thenReturn(Arrays.asList(UUID("493410b3-dd0b-4b78-97bf-289f50f6e74f")));
        Assert.assertEquals(UUIDGenerator.uUID, "493410b3-dd0b-4b78-97bf-289f50f6e74f");
    }

    @Ignore
    @Test
    fun `test it handles null applicationDao`() {
        PowerMockito.`when`(ApplicationDao.get(context)).thenReturn(null);
        val secondUID = UUIDGenerator.uUID
        Assert.assertNotNull(secondUID)
    }

    @Ignore
    @Test
    fun `test it returns non-null when catches an exception`() {
        PowerMockito.`when`<List<UUID>>(uuidDao?.loadAll()).thenThrow(RuntimeException("test exception"))
        val actual = UUIDGenerator.uUID
        Assert.assertNotNull(actual)
    }
}
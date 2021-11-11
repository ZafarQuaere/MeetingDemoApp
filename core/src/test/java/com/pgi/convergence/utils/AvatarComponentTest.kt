package com.pgi.convergence.utils

import android.view.View
import com.pgi.convergence.RobolectricTest
import com.pgi.convergence.TestApplication
import com.squareup.picasso.Picasso
import io.mockk.coEvery
import io.mockk.mockkClass
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.rule.PowerMockRule
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE, application = TestApplication::class)
class AvatarComponentTest: RobolectricTest() {
    @get:Rule
    val rule: PowerMockRule = PowerMockRule()

    private var avatarView: AvatarComponent? = null
    private var profileImages = mutableListOf<String?>()
    private var profileInitials = mutableListOf<String?>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val context = RuntimeEnvironment.application.applicationContext
        avatarView = AvatarComponent(context)
        mockkStatic(Picasso::class)
        val picassoMock = mockkClass(Picasso::class, relaxed = true)
        coEvery {
            Picasso.get()
        } returns picassoMock
        profileImages.clear()
    }

    @Test
    fun `set avatar value for user count 1`() {
        profileImages.add("https://test")
        avatarView?.setAvatarIcon(1, profileImages)
    }

    @Test
    fun `set avatar value for user count 2`() {
        profileImages.add("https://test")
        profileImages.add("https://test")
        avatarView?.setAvatarIcon(2, profileImages)
    }

    @Test
    fun `set avatar value for user count 3`() {
        profileImages.add("https://test")
        profileImages.add("https://test")
        profileImages.add("https://test")
        avatarView?.setAvatarIcon(3, profileImages)
        assertNotNull(avatarView?.avatarIds)
    }

    @Test
    fun `set avatar value for user count 4`() {
        profileImages.add("https://test")
        profileImages.add("https://test")
        profileImages.add("https://test")
        profileImages.add("https://test")
        avatarView?.setAvatarIcon(4, profileImages)
        assertNotNull(avatarView?.avatarIds)
        assertNotNull(avatarView?.avatarDrawables)
        assertEquals(4, avatarView?.avatarDrawables?.size)
        assertEquals(avatarView?.textUserCount?.visibility, View.GONE)
    }

    @Test
    fun `set avatar value for user count greater than 4`() {
        profileImages.add("https://test")
        profileImages.add("https://test")
        profileImages.add("https://test")
        profileImages.add("")
        profileImages.add(null)
        avatarView?.setAvatarIcon(5, profileImages)
        assertEquals("2",avatarView?.textUserCount?.text.toString())
        assertEquals(avatarView?.textUserCount?.visibility, View.VISIBLE)
    }

    @Test
    fun `set avatar value for user count greater than 1`() {
        profileImages.add("")
        profileInitials.add("TT")
        avatarView?.setAvatarIcon(1, profileImages, profileInitials)
        assertNotNull(avatarView?.textNameInitial)
        profileImages.clear()
        profileInitials.clear()
        profileImages.add("")
        profileInitials.add("#")
        avatarView?.setAvatarIcon(1, profileImages, profileInitials)
        assertNotNull(avatarView?.textNameInitial)
    }

    @After
    fun tearDown() {
        unmockkStatic(Picasso::class)
    }
}

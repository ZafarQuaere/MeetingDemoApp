package com.pgi.convergencemeetings.utils

import android.content.Context
import com.pgi.convergencemeetings.RobolectricTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.robolectric.RuntimeEnvironment


class SimpleOrientationListenerTest: RobolectricTest() {

    lateinit var orientation: SimpleOrientationListener
    lateinit var mContext: Context
    var mOrinetation: Int = 0

    @Before
    fun setup() {
        mContext = RuntimeEnvironment.application.getApplicationContext()
        orientation = object : SimpleOrientationListener(mContext, 12344) {
            override fun onChanged(lastOrientation: Int, orientation: Int) {
                mOrinetation = orientation
            }
        }
    }

    @Test
    fun tesOnOrientationPotrait() {
        orientation.onOrientationChanged(70)
        Assert.assertEquals(0, mOrinetation)
    }

    @Test
    fun tesOnOrientationReversePotrait() {
        orientation.onOrientationChanged(120)
        Assert.assertEquals(3, mOrinetation)
    }

    @Test
    fun tesOnOrientationLandScape() {
        orientation.onOrientationChanged(245)
        Assert.assertEquals(2, mOrinetation)
    }

    @Test
    fun tesOnOrientationReverseLandScape() {
        orientation.onOrientationChanged(295)
        Assert.assertEquals(1, mOrinetation)
    }

    @Test
    fun tesOnOrientationPotratOnNetgative() {
        orientation.onOrientationChanged(-10)
        Assert.assertEquals(0, mOrinetation)
    }

    @Test
    fun tesOnOrientationPotratOnGretaerThan315() {
        orientation.onOrientationChanged(320)
        Assert.assertEquals(0, mOrinetation)
    }
}
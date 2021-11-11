package com.pgi.convergencemeetings.utils

import android.content.Context
import android.hardware.SensorManager
import android.view.OrientationEventListener

abstract class SimpleOrientationListener(context: Context?, rate: Int) : OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
    val ORIENTATION_PORTRAIT = 0
    val ORIENTATION_LANDSCAPE = 1
    val ORIENTATION_PORTRAIT_REVERSE = 2
    val ORIENTATION_LANDSCAPE_REVERSE = 3


    private var lastOrientation = 0


    override fun onOrientationChanged(orientation: Int) {
        if (orientation < 0) {
            return  // Flip screen, Not take account
        }

        val curOrientation: Int

        if (orientation <= 70) {
            curOrientation = ORIENTATION_PORTRAIT
        } else if (orientation <= 120) {
            curOrientation = ORIENTATION_LANDSCAPE_REVERSE
        } else if (orientation <= 245) {
            curOrientation = ORIENTATION_PORTRAIT_REVERSE
        } else if (orientation <= 295) {
            curOrientation = ORIENTATION_LANDSCAPE
        } else {
            curOrientation = ORIENTATION_PORTRAIT
        }
        if (curOrientation != lastOrientation) {
            onChanged(lastOrientation, curOrientation)
            lastOrientation = curOrientation
        }
    }

    abstract fun onChanged(lastOrientation: Int, orientation: Int)
}
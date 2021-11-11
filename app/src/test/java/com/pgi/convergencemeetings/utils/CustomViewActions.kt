package com.pgi.convergencemeetings.utils

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation.CENTER_LEFT
import androidx.test.espresso.action.GeneralLocation.CENTER_RIGHT
import androidx.test.espresso.action.Press.FINGER
import androidx.test.espresso.action.Swipe.FAST
import androidx.test.espresso.action.ViewActions.actionWithAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher


private val EDGE_FUZZ_FACTOR = 0.083f

// This is to remove the constarint at least 90 percent of the view's area is displayed to the user
fun click(): ViewAction {
 return object : ViewAction {
    override fun getDescription(): String {
      return "Click action"
    }
    override fun getConstraints(): Matcher<View> {
      return ViewMatchers.isEnabled()
    }
    override fun perform(uiController: UiController, view: View) {
      view.performClick()
    }
  }
}

fun swipeRight(): ViewAction {
  return actionWithAssertions(CustomSwipeRight(
      FAST,
      CENTER_LEFT,
      CENTER_RIGHT,
      FINGER))
}
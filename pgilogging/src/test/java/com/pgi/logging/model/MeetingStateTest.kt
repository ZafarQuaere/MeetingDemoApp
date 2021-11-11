package com.pgi.logging.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MeetingStateTest {

	lateinit var meetingState: MeetingState

	@Test
	fun `test default meeting state`() {
		meetingState = MeetingState()
		assertEquals(meetingState.currentCamerasSubcribed, null)
		assertEquals(meetingState.currentCamerasPublished, null)
	}

	@Test
	fun `test meeting state with value set`() {
		meetingState = MeetingState(1, 2)
		assertEquals(meetingState.currentCamerasPublished, 1)
		assertEquals(meetingState.currentCamerasSubcribed, 2)
	}

	@Test
	fun `test meeting state with max value set`() {
		meetingState = MeetingState(Int.MAX_VALUE, Int.MAX_VALUE)
		assertEquals(meetingState.currentCamerasPublished, Int.MAX_VALUE)
		assertEquals(meetingState.currentCamerasSubcribed, Int.MAX_VALUE)
	}

	@Test
	fun `test meeting state with min value set`() {
		meetingState = MeetingState(Int.MIN_VALUE, Int.MIN_VALUE)
		assertEquals(meetingState.currentCamerasPublished, Int.MIN_VALUE)
		assertEquals(meetingState.currentCamerasSubcribed, Int.MIN_VALUE)
	}

	@Test
	fun `test meeting state with max value plus 1set`() {
		meetingState = MeetingState(Int.MAX_VALUE.plus(2), Int.MAX_VALUE.plus(2))
		assertEquals(meetingState.currentCamerasPublished, Int.MAX_VALUE.plus(2))
		assertEquals(meetingState.currentCamerasSubcribed, Int.MAX_VALUE.plus(2))
	}
}
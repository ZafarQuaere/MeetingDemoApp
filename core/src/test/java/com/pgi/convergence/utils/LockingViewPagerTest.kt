package com.pgi.convergence.utils

import android.content.Context
import android.view.MotionEvent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LockingViewPagerTest {

	lateinit var viewPager: LockingViewPager

	@Before
	fun setup() {
		val context = ApplicationProvider.getApplicationContext<Context>()
		viewPager = LockingViewPager(context)
	}

	@Test
	fun `test oninit`() {
		assertNotNull(viewPager)
		assertEquals(viewPager.isScrollingLocked, false)
	}

	@Test
	fun `test onTouchEvent while scrolling`() {
		assertNotNull(viewPager)
		assertEquals(viewPager.isScrollingLocked, false)
		assertFalse(viewPager.onTouchEvent(MotionEvent.obtain(10L, 2L,0, 0f,0f, 1)))
	}

	@Test
	fun `test onTouchEvent not scrolling`() {
		assertNotNull(viewPager)
		assertEquals(viewPager.isScrollingLocked, false)
		viewPager.isScrollingLocked = true
		assertFalse(viewPager.onTouchEvent(MotionEvent.obtain(10L, 2L,0, 0f,0f, 1)))
	}

	@Test
	fun `test onInterceptTouchEvent while scrolling`() {
		assertNotNull(viewPager)
		assertEquals(viewPager.isScrollingLocked, false)
		assertFalse(viewPager.onInterceptTouchEvent(MotionEvent.obtain(10L, 2L,0, 0f,0f, 1)))
	}

	@Test
	fun `test onInterceptTouchEvent not scrolling`() {
		assertNotNull(viewPager)
		assertEquals(viewPager.isScrollingLocked, false)
		viewPager.isScrollingLocked = true
		assertFalse(viewPager.onInterceptTouchEvent(MotionEvent.obtain(10L, 2L,0, 0f,0f, 1)))
	}


	@Test
	fun `test onInterceptTouchEvent not scrolling exception`() {
		assertNotNull(viewPager)
		assertEquals(viewPager.isScrollingLocked, false)
		assertFalse(viewPager.onInterceptTouchEvent(MotionEvent.obtain(10L, 2L,0, -100f,-2990f, 1)))
	}
}
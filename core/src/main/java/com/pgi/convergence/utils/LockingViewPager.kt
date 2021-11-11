package com.pgi.convergence.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.convergence.core.R

/**
 * This class is a custom view pager implementation which enables view pager to lock on view while sliding.
 */
class LockingViewPager constructor(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {
	/**
	 * This method informs if scrolling is locked or not.
	 *
	 * @return the boolean
	 */
	/**
	 * This method sets the value for scrolling lock.
	 *
	 * @param scrollingLocked the scrolling locked
	 */
	var isScrollingLocked = DEFAULT_SCROLLING_LOCKED

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(ev: MotionEvent): Boolean {
		// Refer:: https://issuetracker.google.com/issues/36931456
		if (!isScrollingLocked) {
			try {
				return super.onTouchEvent(ev)
			} catch (ex: IllegalArgumentException) {
				// Ignore
			}
		}
		return false
	}

	override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
		// Refer:: https://issuetracker.google.com/issues/36931456
		if (!isScrollingLocked) {
			try {
				return super.onInterceptTouchEvent(ev)
			} catch (ex: IllegalArgumentException) {
				// Ignore
			}
		}
		return false
	}

	companion object {
		private const val DEFAULT_SCROLLING_LOCKED = false
	}
	/**
	 * Instantiates a new Locking view pager.
	 *
	 * @param context the context
	 * @param attrs   the attrs
	 */
	/**
	 * Instantiates a new Locking view pager.
	 *
	 * @param context the context
	 */
	init {
		val ta = context.obtainStyledAttributes(attrs, intArrayOf(R.attr.scrollingLocked))
		isScrollingLocked = ta.getBoolean(0, DEFAULT_SCROLLING_LOCKED)
		ta.recycle()
	}
}
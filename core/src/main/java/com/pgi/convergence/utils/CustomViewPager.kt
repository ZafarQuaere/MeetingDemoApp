package com.pgi.convergence.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.pgi.convergence.application.CoreApplication
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue

class CustomViewPager(context: Context, attrs: AttributeSet?) : ViewPager(context, attrs) {
	private val TAG = CustomViewPager::class.java.simpleName
	private val mLogger = CoreApplication.mLogger
	var isPagingEnabled = true

	@SuppressLint("ClickableViewAccessibility")
	override fun onTouchEvent(event: MotionEvent): Boolean {
		return if (isPagingEnabled) super.onTouchEvent(event) else false
	}

	override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
		var handled = false
		try {
			if (isPagingEnabled) {
				handled = super.onInterceptTouchEvent(event)
			}
		} catch (ex: Exception) {
			handled = false
			mLogger.error(TAG, LogEvent.EXCEPTION, LogEventValue.TRUE, "CustomViewPage onInterceptTouchEvent exception: " + ex.message,
					ex, null, true, false)
		}
		return handled
	}

	override fun callOnClick(): Boolean {
		return if (isPagingEnabled) super.callOnClick() else false
	}

}
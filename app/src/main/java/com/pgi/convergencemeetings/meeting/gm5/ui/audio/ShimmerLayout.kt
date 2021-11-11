package com.pgi.convergencemeetings.meeting.gm5.ui.audio

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergencemeetings.R

/**
 * Created by visheshchandra on 12/21/2017.
 */
class ShimmerLayout(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {
	private val mScreenWidth: Int
	private var mAnimation: AnimationSet? = null
	private val ANIMATION_DURATION = 2000
	private val SHIMMER_MOVER_WIDTH = 400

	init {
		val displayMetrics = DisplayMetrics()
		(context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
		mScreenWidth = displayMetrics.widthPixels
		//
		val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(ContextCompat.getColor(context, R.color.shimmer_gradient_1),
		                                                                                            ContextCompat.getColor(context, R.color.shimmer_gradient_2), ContextCompat.getColor(context, R.color.shimmer_gradient_3)))
		val targetView = View(context)
		targetView.layoutParams = LayoutParams(SHIMMER_MOVER_WIDTH, LayoutParams.MATCH_PARENT)
		targetView.background = gradientDrawable
		addView(targetView)
		if(!CoreApplication.isRoboUnitTest) {
			prepareAnimation(context, targetView)
		}
	}

	fun prepareAnimation(context: Context?, targetView: View) {
		mAnimation = AnimationSet(true)
		mAnimation?.interpolator = AccelerateDecelerateInterpolator()
		mAnimation?.fillAfter = true
		val translateAnimation = TranslateAnimation((-SHIMMER_MOVER_WIDTH).toFloat(), mScreenWidth.toFloat(), 0F, 0F)
		translateAnimation.duration = ANIMATION_DURATION.toLong()
		translateAnimation.repeatCount = Animation.INFINITE
		mAnimation?.addAnimation(translateAnimation)
		targetView.animation = mAnimation
	}

	fun setGhostImage(@DrawableRes resId: Int) {
		setBackgroundResource(resId)
	}

	fun startShimmerAnimation() {
		post { mAnimation?.start() }
	}

	fun stopShimmerAnimation() {
		mAnimation?.reset()
	}
}
package com.pgi.convergence.utils

import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.convergence.core.R
import com.google.android.material.snackbar.ContentViewCallback

class SimpleCustomSnackbarView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr), ContentViewCallback {

    lateinit var tvMsg: TextView
    lateinit var tvAction: TextView
    lateinit var layRoot: CoordinatorLayout

    init {
        View.inflate(context, R.layout.custom_snack_bar, this)
        clipToPadding = false
        this.tvMsg = findViewById(R.id.tv_message)
        this.tvAction = findViewById(R.id.tv_action)
        this.layRoot = findViewById(R.id.snack_constraint)
        if(test){
            animateContentIn(2,2)
            animateContentOut(3,3)
        }
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        val animatorSet = AnimatorSet().apply {
            interpolator = OvershootInterpolator()
            setDuration(500)
        }
        animatorSet.start()
    }

    override fun animateContentOut(delay: Int, duration: Int) {
    }

    companion object {
        var test: Boolean = false
    }
}
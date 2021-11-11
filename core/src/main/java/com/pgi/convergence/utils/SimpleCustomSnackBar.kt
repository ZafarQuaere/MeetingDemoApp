package com.pgi.convergence.utils

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar

open class SimpleCustomSnackbar(
		parent: ViewGroup,
		content: SimpleCustomSnackbarView) : BaseTransientBottomBar<SimpleCustomSnackbar>(parent, content, content) {

	init {
		if(!test) {
			getView().setBackgroundColor(ContextCompat.getColor(view.context, android.R.color.transparent))
			getView().setPadding(0, 0, 0, 0)
		}
	}

	companion object {
		var test: Boolean = false
		fun make(view: View,
		         message: String, duration: Int,
		         listener: View.OnClickListener?, action_lable: String?, bg_color: Int): SimpleCustomSnackbar? {
			val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
					"No suitable parent found from the given view. Please provide a valid view.")
			return try {
				val snackbarView = SimpleCustomSnackbarView(view.context)
				snackbarView.tvMsg.text = message
				action_lable?.let {
					snackbarView.tvAction.text = action_lable
					snackbarView.tvAction.setOnClickListener(listener)
				}
				snackbarView.layRoot.setBackgroundColor(bg_color)

				SimpleCustomSnackbar(
						parent,
						snackbarView).setDuration(duration)
			} catch (e: Exception) {
				Log.v("exception ", e.message)
				null
			}
		}
	}
}

internal fun View?.findSuitableParent(): ViewGroup? {
	var view = this
	var fallback: ViewGroup? = null
	do {
		if (view is CoordinatorLayout) {
			return view
		} else if (view is FrameLayout) {
			if (view.id == android.R.id.content) {
				return view
			} else {
				fallback = view
			}
		}

		if (view != null) {
			val parent = view.parent
			view = if (parent is View) parent else null
		}
	} while (view != null)
	return fallback
}

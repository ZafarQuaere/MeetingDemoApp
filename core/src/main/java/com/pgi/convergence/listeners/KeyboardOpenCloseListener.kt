package com.pgi.convergence.listeners

import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener

class KeyboardOpenCloseListener : OnGlobalLayoutListener {
	interface Callback {
		fun onKeyboardHide()
		fun onKeyboardVisible()
	}

	private var mRootView: View? = null
	private var mCallback: Callback? = null
	override fun onGlobalLayout() {
		mRootView?.let { rootView ->
			val heightDiff = rootView.rootView?.height?.minus(rootView.height)
			heightDiff?.let {
				if (it > APPROXIMATE_KEYBOARD_HEIGHT) {
					mCallback?.onKeyboardVisible()
				} else {
					mCallback?.onKeyboardHide()
				}
			}
		}
	}

	fun registerKeyboardOpenCloseListener(rootView: View?, callbackListener: Callback?) {
		mRootView = rootView
		mCallback = callbackListener
		rootView?.viewTreeObserver?.addOnGlobalLayoutListener(this)
	}

	fun unregisterKeyboardOpenCloseListener(rootView: View?) {
		rootView?.viewTreeObserver?.removeGlobalOnLayoutListener(this)
		mRootView = null
		mCallback = null
	}

	companion object {
		private const val APPROXIMATE_KEYBOARD_HEIGHT = 300
	}
}
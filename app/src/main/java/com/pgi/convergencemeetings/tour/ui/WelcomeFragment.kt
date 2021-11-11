package com.pgi.convergencemeetings.tour.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.pgi.convergence.application.CoreApplication
import com.pgi.convergence.utils.LockingViewPager
import com.pgi.convergencemeetings.R
import com.pgi.logging.enums.Interactions
import com.pgi.logging.enums.LogEvent
import com.pgi.logging.enums.LogEventValue

/**
 * This class handle the tour display slider view.
 */
class WelcomeFragment : BindingFragment<WelcomeFragment.Callback>(Callback::class.java), WelcomeSlidesFragment.Callback {
	private lateinit var mFagerAdapter: FragmentPagerAdapter
	private lateinit var mLockingViewPager: LockingViewPager
	private var mHasShownWelcomeSlides = false
	private val mlogger = CoreApplication.mLogger

	/**
	 * The Callback interface for dismiss request, continue click and sign button click events.
	 */
	interface Callback {
		/**
		 * Notify dismiss requested.
		 */
		fun notifyDismissRequested()

		/**
		 * Continue button clicked.
		 */
		fun continueButtonClicked()

		/**
		 * Sign in button clicked.
		 */
		fun signInButtonClicked()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setInterActionName(Interactions.WELCOME_TOUR.interaction)
		mlogger.debug(TAG, LogEvent.APP_VIEW, LogEventValue.WELCOMETOUR, "WelcomeFragment onCreate()",
				null, null, true, false)
		mFagerAdapter = WelcomeFragmentPagerAdapter(childFragmentManager)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_welcome_slides_pager, container, false)
		mLockingViewPager = view.findViewById<View>(R.id.locking_view_pager) as LockingViewPager
		mLockingViewPager.adapter = mFagerAdapter
		mLockingViewPager.addOnPageChangeListener(pageChangeListener)
		return view
	}

	override fun notifyViewingFinalSlide(viewingFinalSlide: Boolean) {
		mLockingViewPager.isScrollingLocked = !viewingFinalSlide
	}

	override fun notifyDismissRequested() {
		val callback = getBinding()
		callback?.notifyDismissRequested()
	}

	override fun continueButtonClicked() {
		val callback = getBinding()
		callback?.continueButtonClicked()
	}

	override fun signInButtonClicked() {
		val callback = getBinding()
		callback?.signInButtonClicked()
	}

	/*
    * Adapter class for tour slides. It uses WelcomeSlidesFragment to display tour slides.
    */
	private inner class WelcomeFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
		private val FRAGMENT_COUNT = 1

		override fun getCount(): Int {
			return FRAGMENT_COUNT
		}

		override fun getItem(position: Int): Fragment {
			return if (POSITION == position) {
				WelcomeSlidesFragment.newInstance(BINDING_FRAGMENT_PARENT)
			} else StaticFragment.newInstance(R.layout.fragment_welcome_slide_empty)
		}
	}

	private val pageChangeListener: OnPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
		override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
			if (POSITION == position && DISMISSAL_POSITION_OFFSET < positionOffset) {
				if (!mHasShownWelcomeSlides) {
					getBinding()?.notifyDismissRequested()
					mHasShownWelcomeSlides = true
				}
			}
		}
	}

	companion object {
		private const val DISMISSAL_POSITION_OFFSET = 0.333f
		private const val POSITION = 0
		private const val POSITION_SIGNIN = 1
		private val TAG = WelcomeFragment::class.java.simpleName
		/**
		 * This method creates a new instance of welcome fragment.
		 *
		 * @return WelcomeFragment
		 */
		fun newInstance(): WelcomeFragment {
			return WelcomeFragment()
		}

		/**
		 * This method creates a new instance of welcome fragment using binding.
		 *
		 * @param binding the binding
		 * @return WelcomeFragment
		 */
		fun newInstance(binding: Int): WelcomeFragment {
			val fragment = WelcomeFragment()
			fragment.arguments = createArguments(binding)
			return fragment
		}
	}
}
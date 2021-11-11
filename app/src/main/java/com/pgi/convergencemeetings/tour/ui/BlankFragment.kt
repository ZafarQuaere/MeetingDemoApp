package com.pgi.convergencemeetings.tour.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.newrelic.agent.android.NewRelic
import com.pgi.convergence.utils.LockingViewPager
import com.pgi.convergencemeetings.R
import com.pgi.logging.enums.Interactions

/**
 * This class shows a blank view to the user
 */
class BlankFragment : BindingFragment<BlankFragment.Callback>(Callback::class.java), WelcomeSlidesFragment.Callback {
	private lateinit var mFagerAdapter: FragmentPagerAdapter
	private lateinit var mLockingViewPager: LockingViewPager
	private var mProgressbar: ProgressBar? = null
	private var mHasShownWelcomeSlides = false
	var showSpinner = false

	interface Callback {
		fun notifyDismissRequested()
		fun continueButtonClicked()
		fun signInButtonClicked()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		NewRelic.setInteractionName(Interactions.ON_BOARDING.interaction)
		mFagerAdapter = BlankFragmentPagerAdapter(childFragmentManager)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_welcome_slides_pager, container, false)
		mLockingViewPager = view.findViewById<View>(R.id.locking_view_pager) as LockingViewPager
		mProgressbar = view.findViewById<View>(R.id.loadingProgress) as ProgressBar
		setProgressBarVisibility(showSpinner)
		mLockingViewPager.adapter = mFagerAdapter
		mLockingViewPager.addOnPageChangeListener(pageChangeListener)
		mLockingViewPager.offscreenPageLimit = 3
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
		val callback =  getBinding()
		callback?.continueButtonClicked()
	}

	override fun signInButtonClicked() {
		val callback =  getBinding()
		callback?.signInButtonClicked()
	}

	fun setProgressBarVisibility(show: Boolean) {
		if (mProgressbar != null) {
			if (show) {
				mProgressbar?.visibility = View.VISIBLE
			} else {
				mProgressbar?.visibility = View.GONE
			}
		}
	}

	private inner class BlankFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
		private val FRAGMENT_COUNT = 1

		override fun getCount(): Int {
			return FRAGMENT_COUNT
		}

		override fun getItem(position: Int): Fragment {
			return if (POSITION == position) {
				StaticFragment.newInstance(R.layout.fragment_welcome_slide_empty)
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
		fun newInstance(): BlankFragment {
			return BlankFragment()
		}

		fun newInstance(binding: Int): BlankFragment {
			val fragment = BlankFragment()
			fragment.arguments = createArguments(binding)
			return fragment
		}
	}
}
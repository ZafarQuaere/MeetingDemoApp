package com.pgi.convergencemeetings.tour.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.utils.CirclePageIndicator
import com.pgi.convergence.utils.CustomViewPager
import com.pgi.convergence.utils.ResourcesUtil
import com.pgi.convergencemeetings.R

/**
 * This class shows tour slides.
 */
class WelcomeSlidesFragment : BindingFragment<WelcomeSlidesFragment.Callback>(Callback::class.java) {
	private lateinit var mPagerAdapter: PagerAdapter
	private var mSharedPreferencesManager = SharedPreferencesManager.getInstance()
	private var mHasShownWelcomeSlides = false

	@BindView(R.id.fl_enter_url_fragment)
	lateinit var flEnterUrl: FrameLayout

	@BindView(R.id.view_pager)
	lateinit var mViewPager: CustomViewPager

	@BindView(R.id.view_pager_indicator)
	lateinit var mViewPagerIndicator: CirclePageIndicator

	@BindView(R.id.button_get_started)
	lateinit var btnContinue: Button

	@BindView(R.id.control_container)
	lateinit var flViewSignin: FrameLayout

	/**
	 * The interface Callback for notifying parent of user actions.
	 */
	interface Callback {
		/**
		 * Notify viewing final slide.
		 *
		 * @param viewingFinalSlide the viewing final slide
		 */
		fun notifyViewingFinalSlide(viewingFinalSlide: Boolean)

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
		loadSlidesIntoAdapter()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_welcome_slides, container, false)
		ButterKnife.bind(this, view)
		mViewPager.adapter = mPagerAdapter
		mViewPagerIndicator.setViewPager(mViewPager)
		mViewPagerIndicator.setOnPageChangeListener(onPageChangeListener)
		mViewPager.offscreenPageLimit = 4
		mHasShownWelcomeSlides = mSharedPreferencesManager.hasShownWelcomeSlides()
		if (mHasShownWelcomeSlides) {
			loadSigninFragment()
		}
		return view
	}

	/**
	 * On click get started.
	 */
	@OnClick(R.id.button_get_started)
	fun onClickGetStarted() {
		if (isBound) {
			when (mViewPager.currentItem) {
				WELCOME_PAGE_POSITION -> if (mHasShownWelcomeSlides) {
					getBinding()?.signInButtonClicked()
				} else {
					mViewPager.currentItem = JOIN_PAGE_POSITION
				}
				JOIN_PAGE_POSITION -> mViewPager.currentItem = AUDIO_PAGE_POSITION
				AUDIO_PAGE_POSITION -> mViewPager.currentItem = CONTENT_PAGE_POSITION
				CONTENT_PAGE_POSITION -> mViewPager.currentItem = CHAT_PAGE_POSITION
				CHAT_PAGE_POSITION -> {
					getBinding()?.continueButtonClicked()
					getBinding()?.notifyDismissRequested()
					btnContinue.isEnabled = false
				}
				else -> {
				}
			}
		}
	}

	private fun loadSigninFragment() {
		btnContinue.text = resources.getString(R.string.sign_in)
		mViewPagerIndicator.alpha = 0f
		mViewPager.isPagingEnabled = false
		mPagerAdapter.notifyDataSetChanged()
	}

	private val onPageChangeListener: OnPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
		override fun onPageSelected(position: Int) {}
		override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
			val isLastSlideDisplayed = isLastSlideDisplayed
			if (isBound) {
				getBinding()?.notifyViewingFinalSlide(isLastSlideDisplayed)
			}
		}
	}

	private inner class WelcomeSlideFragmentPagerAdapter(fragmentManager: FragmentManager, vararg layouts: Int) : StaticFragmentPagerAdapter(fragmentManager, *layouts) {
		override fun createFragment(layoutId: Int): Fragment {
			return StaticNestedFragment.newInstance(R.layout.fragment_welcome_slide_container, layoutId)
		}
	}

	private fun loadSlidesIntoAdapter() {
		val layouts = ResourcesUtil.getResourceIdArray(this, R.array.welcome_screen_layouts)
		mPagerAdapter = WelcomeSlideFragmentPagerAdapter(childFragmentManager, *layouts)
	}

	private val isLastSlideDisplayed: Boolean
		get() = mViewPager.currentItem == mPagerAdapter.count - PAGE_POSITION

	companion object {
		private const val WELCOME_PAGE_POSITION = 0
		private const val JOIN_PAGE_POSITION = 1
		private const val AUDIO_PAGE_POSITION = 2
		private const val CONTENT_PAGE_POSITION = 3
		private const val CHAT_PAGE_POSITION = 4
		private const val PAGE_POSITION = 1

		/**
		 * New instance welcome slides fragment.
		 *
		 * @return the welcome slides fragment
		 */
		fun newInstance(): WelcomeSlidesFragment {
			return WelcomeSlidesFragment()
		}

		/**
		 * New instance welcome slides fragment.
		 *
		 * @param binding the binding
		 * @return the welcome slides fragment
		 */
		fun newInstance(binding: Int): WelcomeSlidesFragment {
			val fragment = WelcomeSlidesFragment()
			fragment.arguments = createArguments(binding)
			return fragment
		}
	}
}
package com.pgi.convergencemeetings.tour.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.pgi.convergencemeetings.tour.ui.StaticFragment.Companion.newInstance

/**
 * Created by nnennaiheke on 2/6/18.
 */
open class StaticFragmentPagerAdapter(fm: FragmentManager, private vararg val mLayouts: Int) : FragmentPagerAdapter(fm) {
	override fun getItem(index: Int): Fragment {
		return createFragment(mLayouts[index])
	}

	override fun getCount(): Int {
		return mLayouts.size
	}

	protected open fun createFragment(layoutId: Int): Fragment {
		return newInstance(layoutId)
	}
}
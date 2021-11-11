package com.pgi.convergencemeetings.meeting.gm5.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

/**
 * Created by ashwanikumar on 8/23/2017.
 */
class WebMeetingTabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
	var mFragments: MutableList<Fragment> = ArrayList()
	override fun getItem(position: Int): Fragment {
		return mFragments[position]
	}

	fun addFragment(fragment: Fragment) {
		mFragments.add(fragment)
	}

	override fun getCount(): Int {
		return mFragments.size
	}

}
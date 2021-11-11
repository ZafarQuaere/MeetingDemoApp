package com.pgi.convergencemeetings.tour.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pgi.convergencemeetings.R

/**
 * This class provides instance of a static nested fragment.
 * This class is used by WelcomeSlidePageAdapter class for displaying slidable tour screens.
 */
class StaticNestedFragment : StaticFragment() {
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(containerIdArgument, container, false)
		if (null == savedInstanceState) {
			val fm = childFragmentManager
			fm.beginTransaction()
					.add(R.id.fragment_container, createNestedFragment())
					.commit()
		}
		return view
	}

	protected val containerIdArgument: Int
		get() {
			val arguments = arguments
			return arguments?.getInt(EXTRA_CONTAINER_ID, DEFAULT_CONTAINER_ID) ?: DEFAULT_CONTAINER_ID
		}

	private fun createNestedFragment(): Fragment {
		return newInstance(layoutIdArgument)
	}

	companion object {
		private const val EXTRA_CONTAINER_ID = "com.pgi.android.app.static_nested_fragment.extra_container_id"
		private const val DEFAULT_CONTAINER_ID = R.layout.container_single_fragment
		fun newInstance(containerId: Int, layoutId: Int): StaticNestedFragment {
			val fragment = StaticNestedFragment()
			fragment.arguments = createArguments(containerId, layoutId)
			return fragment
		}

		private fun createArguments(containerId: Int, layoutId: Int): Bundle? {
			return populateArguments(Bundle(), containerId, layoutId)
		}

		private fun populateArguments(arguments: Bundle?, containerId: Int, layoutId: Int): Bundle? {
			if (null != arguments) {
				populateArguments(arguments, layoutId)
				arguments.putInt(EXTRA_CONTAINER_ID, containerId)
			}
			return arguments
		}
	}
}
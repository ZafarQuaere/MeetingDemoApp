package com.pgi.convergencemeetings.tour.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pgi.convergencemeetings.R

/**
 * This class provides a StaticFragment instance. It represents single fragment inside nested fragment container.
 */
open class StaticFragment : Fragment() {
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val layoutId = layoutIdArgument
		return if (0 != layoutId) inflater.inflate(layoutId, container, false) else super.onCreateView(inflater, container, savedInstanceState)
	}

	/**
	 * Gets layout id argument.
	 *
	 * @return the layout id argument
	 */
	protected val layoutIdArgument: Int
		get() {
			val arguments = arguments
			return arguments?.getInt(EXTRA_LAYOUT_ID, DEFAULT_LAYOUT_ID) ?: DEFAULT_LAYOUT_ID
		}

	companion object {
		private const val EXTRA_LAYOUT_ID = "com.pgi.android.app.static_fragment.extra_layout_id"
		private const val DEFAULT_LAYOUT_ID = 0
		private const val EXTRA_CONTAINER_ID = "com.pgi.android.app.static_nested_fragment.extra_container_id"
		private const val DEFAULT_CONTAINER_ID = R.layout.container_single_fragment

		/**
		 * New instance static fragment.
		 *
		 * @return the static fragment
		 */
		fun newInstance(): StaticFragment {
			return StaticFragment()
		}

		/**
		 * New instance static fragment.
		 *
		 * @param layoutId the layout id
		 * @return the static fragment
		 */
    @JvmStatic
    fun newInstance(layoutId: Int): StaticFragment {
			val arguments = Bundle()
			populateArguments(arguments, layoutId)
			val fragment = StaticFragment()
			fragment.arguments = arguments
			return fragment
		}

		/**
		 * Create arguments bundle.
		 *
		 * @param layoutId the layout id
		 * @return the bundle
		 */
		fun createArguments(layoutId: Int): Bundle? {
			return populateArguments(Bundle(), layoutId)
		}

		/**
		 * Populate arguments bundle.
		 *
		 * @param arguments the arguments
		 * @param layoutId  the layout id
		 * @return the bundle
		 */
		fun populateArguments(arguments: Bundle?, layoutId: Int): Bundle? {
			arguments?.putInt(EXTRA_LAYOUT_ID, layoutId)
			return arguments
		}
	}
}
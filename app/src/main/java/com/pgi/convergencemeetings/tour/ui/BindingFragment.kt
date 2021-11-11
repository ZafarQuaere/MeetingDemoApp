package com.pgi.convergencemeetings.tour.ui

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.newrelic.agent.android.NewRelic
/**
 * Created by nnennaiheke on 2/6/18.
 *
 * @param <T> the type parameter
</T> */
abstract class BindingFragment<T> protected constructor(bindingType: Class<T>) : Fragment() {
	override fun onAttach(activity: Activity) {
		super.onAttach(activity)
		binder.onAttach()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binder.onCreate()
	}

	protected fun setInterActionName(name: String?) {
		NewRelic.setInteractionName(name)
	}

	override fun onDestroy() {
		binder.onDestroy()
		super.onDestroy()
	}

	override fun onDetach() {
		binder.onDetach()
		super.onDetach()
	}

	/**
	 * Is bound boolean.
	 *
	 * @return the boolean
	 */
	protected val isBound: Boolean
		protected get() = null != target

	/**
	 * Gets binding.
	 *
	 * @return the binding
	 */
	protected fun getBinding(): T? {
		return target
	}

	private val binder: FragmentBinder<T>
	private var target: T? = null
	private val binding: FragmentBinder.Binding<T> = object : FragmentBinder.Binding<T> {
		override fun bind(target: T) {
			this@BindingFragment.target = target
		}

		override fun unbind() {
			target = null
		}
	}

	companion object {
		const val BINDING_NONE = FragmentBinder.BINDING_NONE
		const val BINDING_ACTIVITY = FragmentBinder.BINDING_ACTIVITY
		const val BINDING_FRAGMENT_PARENT = FragmentBinder.BINDING_FRAGMENT_PARENT
		const val BINDING_FRAGMENT_TARGET = FragmentBinder.BINDING_FRAGMENT_TARGET

		/**
		 * Create arguments bundle.
		 *
		 * @param binding the binding
		 * @return the bundle
		 */
		fun createArguments(binding: Int): Bundle? {
			return populateArguments(Bundle(), binding)
		}

		/**
		 * Create arguments bundle.
		 *
		 * @param <T>             the type parameter
		 * @param bindingStrategy the binding strategy
		 * @return the bundle
		</T> */
		protected fun <T> createArguments(bindingStrategy: Class<out FragmentBinder.BindingStrategy<T?>?>?): Bundle? {
			return FragmentBinder.createArguments(bindingStrategy)
		}

		/**
		 * Populate arguments bundle.
		 *
		 * @param arguments the arguments
		 * @param binding   the binding
		 * @return the bundle
		 */
		fun populateArguments(arguments: Bundle?, binding: Int): Bundle? {
			return FragmentBinder.populateArguments(arguments, binding)
		}
	}

	/**
	 * Instantiates a new Binding fragment.
	 *
	 * @param bindingType the binding type
	 */
	init {
		binder = FragmentBinder(this, binding, bindingType)
	}
}
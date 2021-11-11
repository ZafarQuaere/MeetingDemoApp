package com.pgi.convergencemeetings.tour.ui

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * This class provide fragment binding functionality.
 *
 * @param <I> the type parameter
</I> */
class FragmentBinder<I>(private val mFragment: Fragment, private val mBinding: Binding<I>, private val mBindingType: Class<I>) {
	private var mBindingStrategy: BindingStrategy<I>? = null

	/**
	 * The callback interface which informs if fragment is bound or unbounded.
	 *
	 * @param <T> the type parameter
	</T> */
	interface Binding<T> {
		/**
		 * Bind.
		 *
		 * @param target the target
		 */
		fun bind(target: T)

		/**
		 * Unbind.
		 */
		fun unbind()
	}

	/**
	 * On attach.
	 */
	fun onAttach() {
		lazyBindingStrategy()!!.onAttach()
	}

	/**
	 * On create.
	 */
	fun onCreate() {
		lazyBindingStrategy()!!.onCreate()
	}

	/**
	 * On destroy.
	 */
	fun onDestroy() {
		lazyBindingStrategy()!!.onDestroy()
	}

	/**
	 * On detach.
	 */
	fun onDetach() {
		lazyBindingStrategy()!!.onDetach()
	}

	/**
	 * The class provides No binding strategy.
	 *
	 * @param <T> the type parameter
	</T> */
	protected class NoBindingStrategy<T>
	/**
	 * Instantiates a new No binding strategy.
	 *
	 * @param fragment    the fragment
	 * @param binding     the binding
	 * @param bindingType the binding type
	 */
	(fragment: Fragment, binding: Binding<T>, bindingType: Class<T>) : BindingStrategy<T>(fragment, binding, bindingType)

	/**
	 * The class provides Binding strategy.
	 *
	 * @param <T> the type parameter
	</T> */
	abstract class BindingStrategy<T>(protected val fragment: Fragment, private val binding: Binding<T>, private val bindingType: Class<T>) {
		/**
		 * On attach.
		 */
		open fun onAttach() {}

		/**
		 * On create.
		 */
		open fun onCreate() {}

		/**
		 * On destroy.
		 */
		open fun onDestroy() {}

		/**
		 * On detach.
		 */
		open fun onDetach() {}

		/**
		 * Bind an object using binding strategy.
		 *
		 * @param target the target
		 */
		protected fun bind(target: Any) {
			try {
				bindingType.cast(target)?.let { binding.bind(it) }
			} catch (exception: ClassCastException) {
				throw IllegalStateException(String.format("The mBinding target of type %s could not be cast to the required target type %s."
						, target.javaClass.simpleName, bindingType.simpleName), exception)
			}
		}

		/**
		 * Unbind.
		 */
		protected fun unbind() {
			binding.unbind()
		}

	}

	/**
	 * The type Activity binding strategy.
	 *
	 * @param <T> the type parameter
	</T> */
	protected class ActivityBindingStrategy<T>(fragment: Fragment, binding: Binding<T>, bindingType: Class<T>) : BindingStrategy<T>(fragment, binding, bindingType) {
		override fun onAttach() {
			bind(fragment.activity!!)
		}

		override fun onDetach() {
			unbind()
		}
	}

	/**
	 * The type Parent fragment binding strategy.
	 *
	 * @param <T> the type parameter
	</T> */
	protected class ParentFragmentBindingStrategy<T>
	/**
	 * Instantiates a new Parent fragment binding strategy.
	 *
	 * @param fragment    the fragment
	 * @param binding     the binding
	 * @param bindingType the binding type
	 */
	(fragment: Fragment, binding: Binding<T>, bindingType: Class<T>) : BindingStrategy<T>(fragment, binding, bindingType) {
		override fun onCreate() {
			bind(fragment.parentFragment!!)
		}

		override fun onDestroy() {
			unbind()
		}
	}

	/**
	 * The type Target fragment binding strategy.
	 *
	 * @param <T> the type parameter
	</T> */
	protected class TargetFragmentBindingStrategy<T>
	/**
	 * Instantiates a new Target fragment binding strategy.
	 *
	 * @param fragment    the fragment
	 * @param binding     the binding
	 * @param bindingType the binding type
	 */
	(fragment: Fragment, binding: Binding<T>, bindingType: Class<T>) : BindingStrategy<T>(fragment, binding, bindingType) {
		override fun onCreate() {
			bind(fragment.targetFragment!!)
		}

		override fun onDestroy() {
			unbind()
		}
	}

	private fun lazyBindingStrategy(): BindingStrategy<I>? {
		if (null == mBindingStrategy) {
			mBindingStrategy = createBindingStrategy(mFragment, mBinding, mBindingType)
		}
		return mBindingStrategy
	}

	companion object {
		const val EXTRA_BINDING_TARGET = "com.pgi.android.app.extra_binding_target"
		const val EXTRA_BINDING_CUSTOM_CLASS = "com.pgi.android.app.extra_binding_custom_class"
		const val BINDING_NONE = 0
		const val BINDING_ACTIVITY = 1
		const val BINDING_FRAGMENT_PARENT = 2
		const val BINDING_FRAGMENT_TARGET = 3
		const val BINDING_FRAGMENT_CUSTOM = 4
		const val BINDING_DEFAULT = BINDING_NONE

		/**
		 * Create arguments bundle.
		 *
		 * @param <T>            the type parameter
		 * @param customStrategy the custom strategy
		 * @return the bundle
		</T> */
		fun <T> createArguments(customStrategy: Class<out BindingStrategy<T?>?>?): Bundle? {
			val arguments = createArguments(BINDING_FRAGMENT_CUSTOM)
			arguments!!.putSerializable(EXTRA_BINDING_CUSTOM_CLASS, customStrategy)
			return arguments
		}

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
		 * Populate arguments bundle.
		 *
		 * @param arguments the arguments
		 * @param binding   the binding
		 * @return the bundle
		 */
		fun populateArguments(arguments: Bundle?, binding: Int): Bundle? {
			arguments?.putInt(EXTRA_BINDING_TARGET, binding)
			return arguments
		}

		/**
		 * Populate binding arguments bundle.
		 *
		 * @param <T>            the type parameter
		 * @param arguments      the arguments
		 * @param customStrategy the custom strategy
		 * @return the bundle
		</T> */
		fun <T> populateArguments(arguments: Bundle?, customStrategy: Class<out BindingStrategy<T?>?>?): Bundle? {
			var args: Bundle? = Bundle()
			if (null != arguments) {
				args = populateArguments(arguments, BINDING_FRAGMENT_CUSTOM)
				args!!.putSerializable(EXTRA_BINDING_CUSTOM_CLASS, customStrategy)
			}
			return args
		}

		private fun <T> createBindingStrategy(fragment: Fragment, binding: Binding<T>, bindingType: Class<T>): BindingStrategy<T>? {
			val arguments = fragment.arguments
			val bindingTarget = getBindingTargetArgument(arguments)
			return when (bindingTarget) {
				BINDING_ACTIVITY -> ActivityBindingStrategy(fragment, binding, bindingType)
				BINDING_FRAGMENT_PARENT -> ParentFragmentBindingStrategy(fragment, binding, bindingType)
				BINDING_FRAGMENT_TARGET -> TargetFragmentBindingStrategy(fragment, binding, bindingType)
				BINDING_FRAGMENT_CUSTOM -> createCustomBindingStrategy(fragment, binding, bindingType)
				BINDING_NONE -> NoBindingStrategy(fragment, binding, bindingType)
				else -> NoBindingStrategy(fragment, binding, bindingType)
			}
		}

		private fun <T> createCustomBindingStrategy(fragment: Fragment, binding: Binding<T>, bindingType: Class<T>): BindingStrategy<T>? {
			try {
				val strategyClass: Class<out BindingStrategy<T>>? = getCustomBindingStrategyArgument(fragment.arguments)
				if (strategyClass != null) {
					return strategyClass.getConstructor(Fragment::class.java, Binding::class.java, Class::class.java).newInstance(fragment, binding, bindingType)
				}
			} catch (ex: Exception) {
				throw IllegalStateException("Cannot instantiate custom mBinding strategy.", ex)
			}
			return null
		}

		private fun getBindingTargetArgument(bundle: Bundle?): Int {
			return bundle?.getInt(EXTRA_BINDING_TARGET, BINDING_DEFAULT) ?: BINDING_DEFAULT
		}

		private fun <T> getCustomBindingStrategyArgument(bundle: Bundle?): Class<out BindingStrategy<T>>? {
			return bundle?.getSerializable(EXTRA_BINDING_CUSTOM_CLASS) as Class<out BindingStrategy<T>> ?: null
		}
	}

}
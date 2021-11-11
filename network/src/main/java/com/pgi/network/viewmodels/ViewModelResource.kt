package com.pgi.network.viewmodels

/**
 * ViewModelResource is an singleton class which hold data and error states for the corresponding viewmodel
 *
 * @author Sudheer R Chilumula
 * @since 5.17
 */
class ViewModelResource<T> private constructor(val status: ViewModelResource.Status, val data: T?, val exception: Throwable?) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {

        fun <T> success(data: T?): ViewModelResource<T> {
            return ViewModelResource(Status.SUCCESS, data, null)
        }

        fun <T> error(exception: Throwable?): ViewModelResource<T> {
            return ViewModelResource(Status.ERROR, null, exception)
        }

        fun <T> loading(data: T?): ViewModelResource<T> {
            return ViewModelResource(Status.LOADING, data, null)
        }
    }
}
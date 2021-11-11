package com.pgi.convergencemeetings.utils

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T?): ResultWrapper<T?>()
    data class Error(val code: Int? = null, val errror: Exception? = null): ResultWrapper<Nothing>()
    object NetworkError: ResultWrapper<Nothing>()
}
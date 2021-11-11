package com.pgi.network.helper

import com.pgi.network.interceptors.NoConnectivityException
import io.reactivex.Observable
import io.reactivex.functions.Function
import retrofit2.HttpException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * RetryAfterTimeoutWithDelay is helper class which retries a observable network call once it fails.
 * For each attempt we add an extra 1000ms delay in making the call.
 *
 * @author Sudheer R Chilumula
 * @since 5.17
 * @see Observable
 * @see Observable.retry
 */
class RetryAfterTimeoutWithDelay(private val maxRetries: Int, var delay: Long, private val delayAmount: Long = 1000)
    : Function<Observable<out Throwable>, Observable<*>> {
    private val TAG = RetryAfterTimeoutWithDelay::class.java.simpleName
    internal var retryCount = 0

    override fun apply(t: Observable<out Throwable>): Observable<*> {
        return t.flatMap {
            if (++retryCount > maxRetries
                || (it is HttpException && (it.code() == 401 || it.code() == 403))
                || (it is UnknownHostException)
                || (it is NoConnectivityException))
            {
                Observable.error(it)
            } else {
                delay += delayAmount
                Observable.timer(delay, TimeUnit.MILLISECONDS)
            }
        }
    }
}
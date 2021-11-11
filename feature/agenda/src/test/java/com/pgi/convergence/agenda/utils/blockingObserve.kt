package com.pgi.convergence.agenda.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.blockingObserve(count: Int = 1): T? {
  var value: T? = null
  val latch = CountDownLatch(count)
  val innerObserver = Observer<T> {
    value = it
    latch.countDown()
  }
  observeForever(innerObserver)
  latch.await(10, TimeUnit.SECONDS)
  return value
}
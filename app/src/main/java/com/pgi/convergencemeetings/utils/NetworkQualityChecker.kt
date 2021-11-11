package com.pgi.convergencemeetings.utils

import com.pgi.convergence.common.features.AppConfig
import com.pgi.convergence.common.features.FeaturesManager
import com.pgi.convergence.utils.ConnectionQuality
import com.pgi.network.repository.FurlProxyRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

// TODO:: Convert this to class instead of singleton
// The only reason this is defined as singleton is, as we cannot inject this in to
// WebMeetingActivity as this is still in java
object NetworkQualityChecker: KoinComponent {
	private var compositeDisposable = CompositeDisposable()
	private val featureManager: FeaturesManager by inject()
	private var bandWidthInterval: Long = 60000
	val connectionQuality: PublishSubject<ConnectionQuality> = PublishSubject.create()
	var quality = ConnectionQuality.UNKNOWN
		private set(value) {
			field = value
			connectionQuality.onNext(value)
		}
	private var features: AppConfig? = null

	init {
		val featuresDisposable = featureManager.appConfigSubject.subscribe {
			features = it
			bandWidthInterval = it?.bandwidth?.meeting?.timerInterval ?: 20000
		}
		compositeDisposable.add(featuresDisposable)
	}

	// TODO:: point this url to download a file or to pgi.com
	fun testNetworkQualityUsingUrl(url: String) {
		if(features?.bandwidth?.meeting?.testEnabled != false) {
			val interval = Observable.interval(0, bandWidthInterval, TimeUnit.MILLISECONDS).subscribe {
				val start = System.nanoTime()
				val disposable = FurlProxyRepository.instance.resolveFurl(url + "/")
						.subscribe({
							determineNetworkQuality(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start).toDouble())
						}, {
							quality = ConnectionQuality.NONE
						})
				compositeDisposable.add(disposable)
			}
			compositeDisposable.add(interval)
		}
	}

	fun stopBandWidthTest() {
		compositeDisposable.clear()
	}

	fun determineNetworkQuality(time: Double) {
		val timeTakenSecs = time / 1000
		val kilobytePerSec = (1024 / timeTakenSecs).roundToInt()
		when {
			kilobytePerSec < 60 -> quality = ConnectionQuality.NONE
			kilobytePerSec < 150 -> quality = ConnectionQuality.POOR
			kilobytePerSec in 150..550 -> quality = ConnectionQuality.MODERATE
			kilobytePerSec in 551..2000 -> quality = ConnectionQuality.GOOD
			kilobytePerSec > 2000 -> quality = ConnectionQuality.EXCELLENT
		}
	}
}
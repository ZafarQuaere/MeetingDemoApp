package com.pgi.convergencemeetings.meeting.gm5.services.audio.softphone

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

object PGiSoftPhoneModel {
    val EMPTY = Any()
    var softPhoneAvailable: BehaviorSubject<Any> = BehaviorSubject.createDefault(EMPTY)
    var softPhoneConnected: BehaviorSubject<Any> = BehaviorSubject.createDefault(EMPTY)
    var softPhoneSignalLevel: BehaviorSubject<Any> = BehaviorSubject.createDefault(EMPTY)
    var softPhoneBadNetworkToast: BehaviorSubject<Any> = BehaviorSubject.createDefault(EMPTY)

    fun getSoftPhoneAvailableEvents(): Observable<Any> {
        return softPhoneAvailable.hide()
    }

    fun getSoftPhoneConnectedEvents(): Observable<Any> {
        return softPhoneConnected.hide()
    }

    fun getSoftPhoneBadNetworkToastEvents(): Observable<Any> {
        return softPhoneBadNetworkToast.hide()
    }

    fun getSoftPhoneSignalLevelEvents(): Observable<Any> {
        return softPhoneSignalLevel.hide()
    }

    fun clear() {
        softPhoneAvailable.onNext(EMPTY)
        softPhoneConnected.onNext(EMPTY)
        softPhoneSignalLevel.onNext(EMPTY)
        softPhoneBadNetworkToast.onNext(EMPTY)
    }
}
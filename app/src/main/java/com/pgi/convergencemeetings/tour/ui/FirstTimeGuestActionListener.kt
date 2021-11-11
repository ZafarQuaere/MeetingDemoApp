package com.pgi.convergencemeetings.tour.ui

import com.pgi.convergencemeetings.models.enterUrlModel.Detail

/**
 * Interface to handle callbacks from Enter Url Fragment [EnterUrlFragment]
 *
 *
 * Created by surbhidhingra on 15-12-17.
 */
interface FirstTimeGuestActionListener {
	/**
	 * On back clicked.
	 */
	fun onBackClicked()

	/**
	 * On enter url fragment skipped.
	 */
	fun onEnterUrlFragmentSkipped()

	/**
	 * On join meeting called.
	 *
	 * @param detail the detail [Detail]
	 */
	fun onJoinMeetingCalled(detail: Detail)
}
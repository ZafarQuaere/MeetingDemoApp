package com.pgi.convergence

import com.pgi.convergence.application.CoreApplication
import com.pgi.logging.PGiLogger
import io.mockk.mockkClass

class TestApplication: CoreApplication() {
	override fun initLogger() {
		mLogger = mockkClass(PGiLogger::class, relaxed = true)
	}
}
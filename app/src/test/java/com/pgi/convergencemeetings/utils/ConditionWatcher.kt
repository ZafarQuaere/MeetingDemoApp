package com.pgi.convergencemeetings.utils

object ConditionWatcher {
    private const val CONDITION_NOT_MET = 0
    private const val CONDITION_MET = 1
    private const val TIMEOUT = 2

    private const val DEFAULT_TIMEOUT_LIMIT = 1000 * 60
    private const val DEFAULT_INTERVAL = 250

    private var timeoutLimit = DEFAULT_TIMEOUT_LIMIT
    private var watchInterval = DEFAULT_INTERVAL

    @Throws(Exception::class)
    fun waitForCondition(checkCondition: () -> Boolean) {
        var status = CONDITION_NOT_MET
        var elapsedTime = 0

        do {
            if (checkCondition()) {
                status = CONDITION_MET
            } else {
                elapsedTime += watchInterval
                Thread.sleep(watchInterval.toLong())
            }

            if (elapsedTime >= timeoutLimit) {
                status = TIMEOUT
                break
            }
        } while (status != CONDITION_MET)

        if (status == TIMEOUT) {
            throw Exception("Instruction took more than " + timeoutLimit / 1000 + " seconds. Test stopped.")
        }
    }
}
package com.pgi.logging.enums

/**
 * Best practices for New Relic dictates not to use more than 5 custom event types/categories
 **/
enum class EventCategory(val category: String) {
    UCCANDROID("UCCAndroid"),
    FEATURE("UCCFeature"),
    METRIC("UCCMetric")
}
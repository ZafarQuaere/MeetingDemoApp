package com.pgi.logging.enums

enum class LoggerLevel(val level: String) {
    OFF("off"),
    FATAL("fatal"),
    ERROR("error"),
    WARN("warn"),
    INFO("info"),
    DEBUG("debug"),
    TRACE("trace")
}
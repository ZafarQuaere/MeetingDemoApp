package com.pgi.logging.enums

enum class Mixpanel(val value: String) {
    NOT_SET(""),
    AUDIO_DIAL_IN("Dial In"),
    AUDIO_DIAL_OUT("Dial Out"),
    AUDIO_DO_NOT_CONNECT("Do not connect my audio"),
    AUDIO_NONE("None"),
    AUDIO_VOIP("VOIP"),
    MEETING_HOST("Host"),
    MEETING_CO_HOST("Cohost"),
    ALL("All"),
    INDIVIDUAL("Individual"),
    DISMISS_MENU("More Menu"),
    DISMISS_SWIPE("Swipe")
}
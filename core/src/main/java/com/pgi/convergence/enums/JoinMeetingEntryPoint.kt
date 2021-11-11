package com.pgi.convergence.enums

enum class JoinMeetingEntryPoint(val entryPoint: String) {
    START_MEETING("Start Meeting"),
    RECENT_MEETINGS("Recent Meetings"),
    HOME_URL("Home - URL"),
    HOME_AGENDA_LIST("Home - Agenda List"),
    HOME_CARD("Home - Card"),
    HOME_CARD_JOIN_BUTTON("HOME_CARD_JOIN_BUTTON"),
    HOME_CARD_LINK("HOME_CARD_LINK"),
    NAME_SEARCH("Name Search"),
    URL_SEARCH("URL Search"),
    ENTERED_URL("Entered URL"),
    EXTERNAL_LINK("External Link"),
    UNKNOWN("Unknown"),
    WAITING_ROOM("WAITING_ROOM");

    fun getValue(): String {
        return entryPoint
    }
}
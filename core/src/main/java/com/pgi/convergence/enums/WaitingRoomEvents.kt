package com.pgi.convergence.enums

enum class WaitingRoomEvents(val index: String) {
    OPEN_MEETING_SECURITY("Open Meeting Security"),
    CANCEL("Cancel"),
    ADMIT_ALL("Admit All"),
    ADMIT("ADMIT"),
    DENY("Deny"),
    ENABLE_RESTRICT_SHARING("Enable Restrict Sharing"),
    DISABLE_RESTRICT_SHARING("Disable Restrict Sharing"),
    LOCK_MEETING("Lock Meeting"),
    UNLOCK_MEETING("Unlock Meeting"),
    ENABLE_WAITING_ROOM("Enable Waiting Room"),
    DISABLE_WAITING_ROOM("Disable Waiting Room")
}
package com.pgi.logging.model

data class MeetingState (
	//We could add this to meeting model. this is to sync with web client
		var currentCamerasPublished: Int? = null,
		var currentCamerasSubcribed: Int? = null,
		var maxCamerasPublished: Int? = null,
		var webcamCount: Int? = null,
		var participantCount: Int? = null,
		var meetingStateCallId: String? = null
)
package com.pgi.convergencemeetings.meeting

import android.content.Context
import android.util.Log
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.UpdateMeetingService
import com.pgi.convergencemeetings.meeting.gm4.services.legacyws.UpdateMeetingServiceCallbacks
import com.pgi.convergencemeetings.models.BaseModel
import com.pgi.convergencemeetings.models.CreateMeetingResult
import com.pgi.convergencemeetings.models.Meeting
import com.pgi.convergencemeetings.models.MeetingRoomGetResult
import com.pgi.convergencemeetings.services.*
import com.pgi.convergencemeetings.utils.AppAuthUtils

/**
 * Created by visheshchandra on 1/11/2018.
 */
abstract class BaseMeetingPresenter(context: Context?) : FetchMeetingRoomServiceCallbacks<BaseModel>,
																												 UpdateMeetingServiceCallbacks<BaseModel>,
																												 CreateMeetingServiceCallbacks<BaseModel> {

	private val TAG = BaseMeetingPresenter::class.java.simpleName
	private val sNoOfRetries = 0
	private val mAppAuthUtils: AppAuthUtils = AppAuthUtils.getInstance()
	private val mCreateMeetingService: CreateMeetingService = CreateMeetingService(this)
	private val mUpdateMeetingService: UpdateMeetingService = UpdateMeetingService(this)
	private val mFetchMeetingRoomService: FetchMeetingRoomService = FetchMeetingRoomService(this)
	protected fun fetchMeetingRoom(meetingRoomId: String?) {
		mFetchMeetingRoomService.fetchMeetingRoom(meetingRoomId)
	}

	protected fun updateMeeting(desktopMeetingId: String?) {
		val meeting = getUpdateMeetingModel(true, false)
		mUpdateMeetingService.updateMeeting(desktopMeetingId, meeting)
	}

	protected fun createMeeting(meetingRoomGetResult: MeetingRoomGetResult?) {
		if (meetingRoomGetResult != null) {
			val meetingRoomUrls = meetingRoomGetResult.meetingRoomUrls
			val meetingRoomDetail = meetingRoomGetResult.meetingRoomDetail
			if (meetingRoomUrls != null && meetingRoomDetail != null) {
				val meetingUrl = meetingRoomUrls.attendeeJoinUrl
				val description = meetingRoomDetail.conferenceTitle
				val meeting = getCreateMeetingModel(meetingUrl, description)
				mCreateMeetingService.createMeeting(meeting)
			}
		}
	}

	private fun getUpdateMeetingModel(incrementAttended: Boolean, favorite: Boolean): Meeting {
		val meeting = Meeting()
		meeting.isIncrementAttended = incrementAttended
		meeting.isFavorite = favorite
		return meeting
	}

	private fun getCreateMeetingModel(meetingUrl: String, description: String): Meeting {
		val meeting = Meeting()
		meeting.meetingUrl = meetingUrl
		meeting.description = description
		meeting.meetingType = "Web"
		meeting.isFavorite = false
		return meeting
	}

	override fun onUpdateMeetingSuccess() {
		//This is background task, No need to update UI in any case, Added Log only for debugging purpose.
		Log.d(TAG, "onUpdateMeetingSuccess")
	}

	override fun onUpdateMeetingError(errorMsg: String) {
		//This is background task, No need to update UI in any case, Added Log only for debugging purpose.
		Log.d(TAG, errorMsg)
	}

	override fun onCreateMeetingSuccess(createMeetingResult: CreateMeetingResult) {
		updateMeeting(createMeetingResult.desktopMeetingId.toString())
	}

	override fun onCreateMeetingError(errorMsg: String) {
		//This is background task, No need to update UI in any case, Added Log only for debugging purpose.
		Log.d(TAG, errorMsg)
	}

	override fun onFetchMeetingRoomSuccess(result: MeetingRoomGetResult) {
		createMeeting(result)
	}

	override fun onFetchMeetingRoomErrorCallback(errorMsg: String) {
		//This is background task, No need to update UI in any case, Added Log only for debugging purpose.
		Log.d(TAG, errorMsg)
	}
}
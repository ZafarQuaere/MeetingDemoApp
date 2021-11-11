package com.pgi.network.models

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.pgi.network.R

/**
 * Created by Suganya R on 4/27/2021.
 * PGi
 * suganya.r@pgi.com
 */

data class GMMeetingInfoGetResponse(
        @SerializedName("MeetingRoomGetResult")
        val meetingRoomGetResult: MeetingRoomGetResult
)
data class MeetingRoomGetResult(
        @SerializedName("MeetingRoomId")
        val meetingRoomId: Int?,
        @SerializedName("MeetingRoomUrls")
        val meetingRoomUrls: MeetingRoomUrl?,
        @SerializedName("MeetingRoomDetail")
        val meetingRoomDetail: MeetingRoomDetail?,
        @SerializedName("AudioDetail")
        val audioDetail: AudioDetail?
)
data class MeetingRoomUrl(
        @SerializedName("VRCUrl")
        val vrcUrl: String
)
data class MeetingRoomDetail(
        @SerializedName("EnableVrc")
        val enableVrc: Boolean?,
        @SerializedName("ConferenceTitle")
        val conferenceTitle: String
)
data class AudioDetail(
        @SerializedName("ParticipantPassCode")
        val participantPassCode: String,
        @SerializedName("PrimaryAccessNumber")
        val primaryAccessNumber: String,
        @SerializedName("phoneInformation")
        val phoneInformation: List<PhoneInformation>
)
data class PhoneInformation(
        @SerializedName("CustomLocation") val customLocation: String?,
        @SerializedName("Location") val location: String,
        @SerializedName("PhoneNumber") val phoneNumber: String,
        @SerializedName("PhoneType") val phoneType: String
)

fun PhoneInformation.localizedPhoneTye(context: Context): String {
        return when (phoneType.toLowerCase()) {
                "toll" -> context.getString(R.string.toll_number)
                "international toll free" -> context.getString(R.string.international_toll_free)
                "local" -> context.getString(R.string.local_number)
                "lo-call" -> context.getString(R.string.lo_call_number)
                else -> context.getString(R.string.toll_number)
        }
}


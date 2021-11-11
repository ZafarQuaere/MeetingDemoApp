package com.pgi.network.models

/**
 * Created by Sudheer Chilumula on 9/6/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */

data class SearchResponse(
        val totalCount: Int,
        val items: List<SearchResponseItem>
)

data class SearchResponseItem(
        val id: String,
        val resourceUri: String,
        val resourceType: String,
        val details: Detail
)

data class SuggestResponse (
        val id: String,
        val resourceUri: String,
        val resourceType: String,
        val title: String,
        val secondaryTitle: String,
        val externalUrl: String,
        val imageUrl: String?,
        val metadata: MetaData
)

data class MeetingRoomInfo (
        val id: String,
        val resourceUri: String,
        val resourceType: String,
        val details: MetaData
)

data class Detail(
        val title: String,
        val secondaryTitle: String,
        val externalUrl: String,
        val imageUrl: String?,
        val metadata: MetaData
)

data class MetaData(
        val meetingRoomName: String?,
        val meetingRoomUrl: String?,
        val meetingRoomType: String,
        val webMeetingServer: String,
        val conferenceType: String,
        val conferenceId: String,
        val ownerEmail: String?,
        val ownerGivenName: String?,
        val ownerFamilyName: String?,
        val brandId: String?,
        val images: List<GMSearchImages> = emptyList()
)

data class GMSearchImages(
        val imageUrl: String?,
        val isDefault: Boolean = false
)
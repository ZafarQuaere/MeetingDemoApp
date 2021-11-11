package com.pgi.convergencemeetings.meeting.gm5.data.model

import java.util.*


/**
 * Created by Sudheer Chilumula on 9/21/18.
 * PGi
 * sudheer.chilumula@pgi.com
 */

data class User(
		var id: String? = null,
		var firstName: String? = null,
		var lastName: String? = null,
		var name: String? = null,
		var initials: String? = null,
		var email: String? = null,
		var profileImage: String? = null,
		var roomRole: String? = null,
		var delegateRole: Boolean = false,
		var promoted: Boolean = false,
		var audio: Audio = Audio(),
		var timestamp: String = Date().toString(),
		var isSelf: Boolean = false,
		var isSharing: Boolean = false,
		var controls: Controls = Controls(),
		var hasControls: Boolean = false,
		var reconnected: Boolean = false,
		var active: Boolean = false,
		var isInAudioWaitingRoom: Boolean = false
							 )

data class Audio(
		var id: String? = null,
		var confId: String? = null,
		var subConfId: String? = null,
		var company: String? = null,
		var partType: String? = null,
		var phone: String? = null,
		var phoneExt: String? = null,
		var ani: String? = null,
		var dnis: String? = null,
		var codec: String? = null,
		var hold: Boolean = false,
		var mute: Boolean = false,
		var listenOnly: Boolean = false,
		var listenLevel: Int = 1,
		var voiceLevel: Int = 1,
		var dialoutId: String? = null,
		var isConnecting: Boolean = false,
		var isConnected: Boolean = false,
		var isVoip: Boolean = false,
		var isDialOut: Boolean = false,
		var isDialIn: Boolean = false
								)

data class Controls(
		var isMuted: Boolean = false,
		var isSpeakerActive: Boolean = false,
		var isChangeAudioActive: Boolean = false,
		val isExitBtnActive: Boolean = false,
		val isOverflowActive: Boolean = false,
		val isFullScreenActive: Boolean = false,
		val isMinScreenActive: Boolean = false,
		val isChatSendEnabled: Boolean = false
									 )

/*
data class Chat(
		var conversationId: String? = null,
		var chatMsg: String? = null,
		var msgState: ChatMessageState = ChatMessageState.RECEIVED,
		var timestamp: String = Date().toString(),
		var user: User = User()
							 )
*/
data class Content(
		var id: String = "",
		var type: String? = null,
		var version: String? = null,
		var visible: Boolean = false,
		var stageId: String? = null,
		var staticMetadata: StaticMetaData = StaticMetaData(),
		var dynamicMetaData: DynamicMetaData = DynamicMetaData(),
		var allowGuestUpdate: Boolean = false,
		var user: User = User()
									)

data class StaticMetaData(
		var streamSessionId: String? = null,
		var documentUrl: String? = null,
		var field: String? = null,
		var title: String? = null,
		val fileId: String? = null,
		val fileObj: FileObj? = null
												 )

data class FileObj(
		val clientId: String?,
		val companyId: String?,
		val id: String?,
		val media: Media?,
		val metaData: MetaData?,
		val name: String?
									)

data class MetaData(
		val contentType: String?,
		val createdOn: String?,
		val lastModifiedOn: String?,
		val presentationState: String?,
		val presentationType: String?,
		val size: String?
									 )

data class Media(
		val downloadUrl: String?,
		val presentationUrl: String?
								)

data class DynamicMetaData(
		var type: String? = null,
		var action: String? = null,
		var newPage: String? = null,
		var screenPresenter: ScreenPresenter = ScreenPresenter(),
		var playState: String? = null,
		var seekTime: String? = null,
		var playOnSeek: Boolean = false,
		var timeCode: Int = 0,
		var eventTime: Long = -1,
		var whiteboardPresenter: WhiteboardPresenter = WhiteboardPresenter()
													)

data class ScreenPresenter(
		var partId: String? = null,
		var name: String? = null,
		var phoneNumber: String? = null,
		var email: String? = null
													)

data class ActiveTalker(
		var user: User,
		var isTalking: Boolean
											 )

data class WhiteboardPresenter(
		var partId: String? = null,
		var name: String? = null,
		var phoneNumber: String? = null,
		var email: String? = null
)

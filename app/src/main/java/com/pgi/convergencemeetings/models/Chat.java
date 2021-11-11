package com.pgi.convergencemeetings.models;

import com.pgi.convergence.constants.AppConstants;
import com.pgi.convergence.enums.ChatMessageState;

public class Chat {

    private String firstName;
    private String lastName;
    private String message;
    private String initials;
    private String timestamp;
    private String webPartId;
    private boolean self;
    private String conversationId;
    private ChatMessageState chatMessageState = ChatMessageState.RECEIVED;
    private boolean isHostOrCoHost;
    public int unReadChatCount = 0;
    private boolean offline = false;
    private String offlineTimestamp = AppConstants.EMPTY_STRING;

    public String getOfflineTimestamp() {
        return offlineTimestamp;
    }

    public void setOfflineTimestamp(String offlineTimestamp) {
        this.offlineTimestamp = offlineTimestamp;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isHostOrCoHost() {
        return isHostOrCoHost;
    }

    public void setHostOrCoHost(boolean hostOrCoHost) {
        isHostOrCoHost = hostOrCoHost;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    private String profileImage;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getWebPartId() {
        return webPartId;
    }

    public void setWebPartId(String webPartId) {
        this.webPartId = webPartId;
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public ChatMessageState getChatMessageState() {
        return chatMessageState;
    }

    public void setChatMessageState(ChatMessageState chatMessageState) {
        this.chatMessageState = chatMessageState;
    }
}
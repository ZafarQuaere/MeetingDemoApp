package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by nnennaiheke on 8/3/17.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "AnonymousEvaluation",
    "AudioConferenceId",
    "AudioProviderType",
    "AutoAcceptRegistrants",
    "Availability",
    "Category",
    "ConferenceDescription",
    "ConferenceTitle",
    "InvitationMessage",
    "InvitationSubject",
    "MaxParticipants",
    "MaxRegistrants",
    "ModeratorEmail",
    "ModeratorInvitationMessage",
    "ModeratorInvitationSubject",
    "ModeratorName",
    "ModeratorSecurityCode",
    "PONumber",
    "RecordAllEvents",
    "RegistrationClosedDateTime",
    "RoomName",
    "SecurityCode",
    "SendEmailOnRegistration",
    "TimeZone",
    "WebProviderType",
    "WebConferenceOptions",
    "Secure",
    "DefaultLanguage",
    "UseHtml5"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class MeetingRoomDetail {
    @JsonIgnore
    @Id(autoincrement = true)
    private Long id;

    private String meetingRoomDetailId;

    @JsonProperty("AnonymousEvaluation")
    private boolean anonymousEvaluation;
    @JsonProperty("AudioConferenceId")
    private int audioConferenceId;
    @JsonProperty("AudioProviderType")
    private String audioProviderType;
    @JsonProperty("AutoAcceptRegistrants")
    private boolean autoAcceptRegistrants;
    @JsonProperty("Availability")
    private String availability;
    @JsonProperty("Category")
    private String category;
    @JsonProperty("ConferenceDescription")
    private String conferenceDescription;
    @JsonProperty("ConferenceTitle")
    private String conferenceTitle;
    @JsonProperty("InvitationMessage")
    private String invitationMessage;
    @JsonProperty("InvitationSubject")
    private String invitationSubject;
    @JsonProperty("MaxParticipants")
    private int maxParticipants;
    @JsonProperty("MaxRegistrants")
    private int maxRegistrants;
    @JsonProperty("ModeratorEmail")
    private String moderatorEmail;
    @JsonProperty("ModeratorInvitationMessage")
    private String moderatorInvitationMessage;
    @JsonProperty("ModeratorInvitationSubject")
    private String moderatorInvitationSubject;
    @JsonProperty("ModeratorName")
    private String moderatorName;
    @JsonProperty("ModeratorSecurityCode")
    private String moderatorSecurityCode;
    @JsonProperty("PONumber")
    private String pONumber;
    @JsonProperty("RecordAllEvents")
    private boolean recordAllEvents;
    @JsonProperty("RegistrationClosedDateTime")
    private String registrationClosedDateTime;
    @JsonProperty("RoomName")
    private String roomName;
    @JsonProperty("SecurityCode")
    private String securityCode;
    @JsonProperty("SendEmailOnRegistration")
    private boolean sendEmailOnRegistration;
    @JsonProperty("TimeZone")
    private String timeZone;
    @JsonProperty("WebProviderType")
    private String webProviderType;

    @ToMany(referencedJoinProperty = "webConferenceOptionsId")
    @JsonProperty("WebConferenceOptions")
    private List<WebConferenceOption> webConferenceOptions = null;

    @JsonProperty("Secure")
    private boolean secure;
    @JsonProperty("DefaultLanguage")
    private String defaultLanguage;
    @JsonProperty("UseHtml5")
    private boolean useHtml5;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1294175641)
    private transient MeetingRoomDetailDao myDao;


    @Generated(hash = 1085273568)
    public MeetingRoomDetail(Long id, String meetingRoomDetailId, boolean anonymousEvaluation,
                             int audioConferenceId, String audioProviderType, boolean autoAcceptRegistrants, String availability,
                             String category, String conferenceDescription, String conferenceTitle, String invitationMessage,
                             String invitationSubject, int maxParticipants, int maxRegistrants, String moderatorEmail,
                             String moderatorInvitationMessage, String moderatorInvitationSubject, String moderatorName,
                             String moderatorSecurityCode, String pONumber, boolean recordAllEvents,
                             String registrationClosedDateTime, String roomName, String securityCode,
                             boolean sendEmailOnRegistration, String timeZone, String webProviderType, boolean secure,
                             String defaultLanguage, boolean useHtml5) {
        this.id = id;
        this.meetingRoomDetailId = meetingRoomDetailId;
        this.anonymousEvaluation = anonymousEvaluation;
        this.audioConferenceId = audioConferenceId;
        this.audioProviderType = audioProviderType;
        this.autoAcceptRegistrants = autoAcceptRegistrants;
        this.availability = availability;
        this.category = category;
        this.conferenceDescription = conferenceDescription;
        this.conferenceTitle = conferenceTitle;
        this.invitationMessage = invitationMessage;
        this.invitationSubject = invitationSubject;
        this.maxParticipants = maxParticipants;
        this.maxRegistrants = maxRegistrants;
        this.moderatorEmail = moderatorEmail;
        this.moderatorInvitationMessage = moderatorInvitationMessage;
        this.moderatorInvitationSubject = moderatorInvitationSubject;
        this.moderatorName = moderatorName;
        this.moderatorSecurityCode = moderatorSecurityCode;
        this.pONumber = pONumber;
        this.recordAllEvents = recordAllEvents;
        this.registrationClosedDateTime = registrationClosedDateTime;
        this.roomName = roomName;
        this.securityCode = securityCode;
        this.sendEmailOnRegistration = sendEmailOnRegistration;
        this.timeZone = timeZone;
        this.webProviderType = webProviderType;
        this.secure = secure;
        this.defaultLanguage = defaultLanguage;
        this.useHtml5 = useHtml5;
    }

    @Generated(hash = 1559344090)
    public MeetingRoomDetail() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeetingRoomDetailId() {
        return meetingRoomDetailId;
    }

    public void setMeetingRoomDetailId(String meetingRoomDetailId) {
        this.meetingRoomDetailId = meetingRoomDetailId;
    }

    public String getpONumber() {
        return pONumber;
    }

    public void setpONumber(String pONumber) {
        this.pONumber = pONumber;
    }

    @JsonProperty("AnonymousEvaluation")
    public boolean isAnonymousEvaluation() {
        return anonymousEvaluation;
    }

    @JsonProperty("AnonymousEvaluation")
    public void setAnonymousEvaluation(boolean anonymousEvaluation) {
        this.anonymousEvaluation = anonymousEvaluation;
    }

    @JsonProperty("AudioConferenceId")
    public int getAudioConferenceId() {
        return audioConferenceId;
    }

    @JsonProperty("AudioConferenceId")
    public void setAudioConferenceId(int audioConferenceId) {
        this.audioConferenceId = audioConferenceId;
    }

    @JsonProperty("AudioProviderType")
    public String getAudioProviderType() {
        return audioProviderType;
    }

    @JsonProperty("AudioProviderType")
    public void setAudioProviderType(String audioProviderType) {
        this.audioProviderType = audioProviderType;
    }

    @JsonProperty("AutoAcceptRegistrants")
    public boolean isAutoAcceptRegistrants() {
        return autoAcceptRegistrants;
    }

    @JsonProperty("AutoAcceptRegistrants")
    public void setAutoAcceptRegistrants(boolean autoAcceptRegistrants) {
        this.autoAcceptRegistrants = autoAcceptRegistrants;
    }

    @JsonProperty("Availability")
    public String getAvailability() {
        return availability;
    }

    @JsonProperty("Availability")
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @JsonProperty("Category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("Category")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("ConferenceDescription")
    public String getConferenceDescription() {
        return conferenceDescription;
    }

    @JsonProperty("ConferenceDescription")
    public void setConferenceDescription(String conferenceDescription) {
        this.conferenceDescription = conferenceDescription;
    }

    @JsonProperty("ConferenceTitle")
    public String getConferenceTitle() {
        return conferenceTitle;
    }

    @JsonProperty("ConferenceTitle")
    public void setConferenceTitle(String conferenceTitle) {
        this.conferenceTitle = conferenceTitle;
    }

    @JsonProperty("InvitationMessage")
    public String getInvitationMessage() {
        return invitationMessage;
    }

    @JsonProperty("InvitationMessage")
    public void setInvitationMessage(String invitationMessage) {
        this.invitationMessage = invitationMessage;
    }

    @JsonProperty("InvitationSubject")
    public String getInvitationSubject() {
        return invitationSubject;
    }

    @JsonProperty("InvitationSubject")
    public void setInvitationSubject(String invitationSubject) {
        this.invitationSubject = invitationSubject;
    }

    @JsonProperty("MaxParticipants")
    public int getMaxParticipants() {
        return maxParticipants;
    }

    @JsonProperty("MaxParticipants")
    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    @JsonProperty("MaxRegistrants")
    public int getMaxRegistrants() {
        return maxRegistrants;
    }

    @JsonProperty("MaxRegistrants")
    public void setMaxRegistrants(int maxRegistrants) {
        this.maxRegistrants = maxRegistrants;
    }

    @JsonProperty("ModeratorEmail")
    public String getModeratorEmail() {
        return moderatorEmail;
    }

    @JsonProperty("ModeratorEmail")
    public void setModeratorEmail(String moderatorEmail) {
        this.moderatorEmail = moderatorEmail;
    }

    @JsonProperty("ModeratorInvitationMessage")
    public String getModeratorInvitationMessage() {
        return moderatorInvitationMessage;
    }

    @JsonProperty("ModeratorInvitationMessage")
    public void setModeratorInvitationMessage(String moderatorInvitationMessage) {
        this.moderatorInvitationMessage = moderatorInvitationMessage;
    }

    @JsonProperty("ModeratorInvitationSubject")
    public String getModeratorInvitationSubject() {
        return moderatorInvitationSubject;
    }

    @JsonProperty("ModeratorInvitationSubject")
    public void setModeratorInvitationSubject(String moderatorInvitationSubject) {
        this.moderatorInvitationSubject = moderatorInvitationSubject;
    }

    @JsonProperty("ModeratorName")
    public String getModeratorName() {
        return moderatorName;
    }

    @JsonProperty("ModeratorName")
    public void setModeratorName(String moderatorName) {
        this.moderatorName = moderatorName;
    }

    @JsonProperty("ModeratorSecurityCode")
    public String getModeratorSecurityCode() {
        return moderatorSecurityCode;
    }

    @JsonProperty("ModeratorSecurityCode")
    public void setModeratorSecurityCode(String moderatorSecurityCode) {
        this.moderatorSecurityCode = moderatorSecurityCode;
    }

    @JsonProperty("PONumber")
    public String getPONumber() {
        return pONumber;
    }

    @JsonProperty("PONumber")
    public void setPONumber(String pONumber) {
        this.pONumber = pONumber;
    }

    @JsonProperty("RecordAllEvents")
    public boolean isRecordAllEvents() {
        return recordAllEvents;
    }

    @JsonProperty("RecordAllEvents")
    public void setRecordAllEvents(boolean recordAllEvents) {
        this.recordAllEvents = recordAllEvents;
    }

    @JsonProperty("RegistrationClosedDateTime")
    public String getRegistrationClosedDateTime() {
        return registrationClosedDateTime;
    }

    @JsonProperty("RegistrationClosedDateTime")
    public void setRegistrationClosedDateTime(String registrationClosedDateTime) {
        this.registrationClosedDateTime = registrationClosedDateTime;
    }

    @JsonProperty("RoomName")
    public String getRoomName() {
        return roomName;
    }

    @JsonProperty("RoomName")
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @JsonProperty("SecurityCode")
    public String getSecurityCode() {
        return securityCode;
    }

    @JsonProperty("SecurityCode")
    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    @JsonProperty("SendEmailOnRegistration")
    public boolean isSendEmailOnRegistration() {
        return sendEmailOnRegistration;
    }

    @JsonProperty("SendEmailOnRegistration")
    public void setSendEmailOnRegistration(boolean sendEmailOnRegistration) {
        this.sendEmailOnRegistration = sendEmailOnRegistration;
    }

    @JsonProperty("TimeZone")
    public String getTimeZone() {
        return timeZone;
    }

    @JsonProperty("TimeZone")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @JsonProperty("WebProviderType")
    public String getWebProviderType() {
        return webProviderType;
    }

    @JsonProperty("WebProviderType")
    public void setWebProviderType(String webProviderType) {
        this.webProviderType = webProviderType;
    }

    @Keep
    @JsonProperty("WebConferenceOptions")
    public List<WebConferenceOption> getWebConferenceOptions() {
        return webConferenceOptions;
    }

    @Keep
    @JsonProperty("WebConferenceOptions")
    public void setWebConferenceOptions(List<WebConferenceOption> webConferenceOptions) {
        this.webConferenceOptions = webConferenceOptions;
    }

    @JsonProperty("Secure")
    public boolean isSecure() {
        return secure;
    }

    @JsonProperty("Secure")
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @JsonProperty("DefaultLanguage")
    public String  getDefaultLanguage() {
        return defaultLanguage;
    }

    @JsonProperty("DefaultLanguage")
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    @JsonProperty("UseHtml5")
    public boolean isUseHtml5() {
        return useHtml5;
    }

    @JsonProperty("UseHtml5")
    public void setUseHtml5(boolean useHtml5) {
        this.useHtml5 = useHtml5;
    }

    public boolean getAnonymousEvaluation() {
        return this.anonymousEvaluation;
    }

    public boolean getAutoAcceptRegistrants() {
        return this.autoAcceptRegistrants;
    }

    public boolean getRecordAllEvents() {
        return this.recordAllEvents;
    }

    public boolean getSendEmailOnRegistration() {
        return this.sendEmailOnRegistration;
    }

    public boolean getSecure() {
        return this.secure;
    }

    public boolean getUseHtml5() {
        return this.useHtml5;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1668444484)
    public synchronized void resetWebConferenceOptions() {
        webConferenceOptions = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 193360389)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMeetingRoomDetailDao() : null;
    }

}
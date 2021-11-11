package com.pgi.convergencemeetings.models;

/**
 * Created by nnennaiheke on 8/2/17.
 */

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
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "CorrelationId",
    "Errors",
    "ExecutionTime",
    "MessageId",
    "ServerDateTime",
    "AttendeeStartPermission",
    "AttendeeWebPassCode",
    "Attendees",
    "AudioDetail",
    "ClientId",
    "ConferenceOptions",
    "HubId",
    "HubName",
    "MeetingRoomDetail",
    "MeetingRoomId",
    "MeetingRoomUrls",
    "Packages",
    "PresenterStartPermission",
    "Presenters",
    "Ptok",
    "Status",
    "WebMeetingServer",
    "ParticipantAnonymity",
    "CustomAudioData",
    "HubBrandId",
    "DeleteReason",
    "DeletedDateTime"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class MeetingRoomGetResult {

    @JsonIgnore

    @Id(autoincrement = true)
    @JsonProperty
    private Long id;

    @JsonProperty
    private String meetingRoomUrlsId;

    @JsonProperty
    private String audioDetailId;

    @JsonProperty
    private String meetingRoomDetailId;

    @JsonProperty("CorrelationId")
    private String correlationId;

    @ToMany(referencedJoinProperty = "errorsId")
    @JsonProperty("Errors")
    private List<Errors> errors = null;

    @JsonProperty("ExecutionTime")
    private int executionTime;
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("ServerDateTime")
    private String serverDateTime;
    @JsonProperty("AttendeeStartPermission")
    private String attendeeStartPermission;
    @JsonProperty("AttendeeWebPassCode")
    private String attendeeWebPassCode;

    @ToMany(referencedJoinProperty = "attendeesId")
    @JsonProperty("Attendees")
    private List<Attendee> attendees = null;

    @ToOne(joinProperty = "audioDetailId" )
    @JsonProperty("AudioDetail")
    private AudioDetail audioDetail;

    @JsonProperty("ClientId")
    private String clientId;

    @ToMany(referencedJoinProperty = "conferenceOptionsId" )

    @JsonProperty("ConferenceOptions")
    private List<ConferenceOption> conferenceOptions = null;

    @JsonProperty("HubId")
    private int hubId;
    @JsonProperty("HubName")
    private String hubName;

    @ToOne(joinProperty = "meetingRoomDetailId")
    @JsonProperty("MeetingRoomDetail")
    private MeetingRoomDetail meetingRoomDetail;

    @JsonProperty("MeetingRoomId")
    private int meetingRoomId;

    @ToOne(joinProperty = "meetingRoomUrlsId")
    @JsonProperty("MeetingRoomUrls")
    private MeetingRoomUrls meetingRoomUrls;

    @ToMany(referencedJoinProperty = "packagesId")
    @JsonProperty("Packages")
    private List<Package> packages = null;

    @JsonProperty("PresenterStartPermission")
    private String presenterStartPermission;

    @ToMany(referencedJoinProperty = "presentersId")
    @JsonProperty("Presenters")
    private List<Presenter> presenters = null;

    @JsonProperty("Ptok")
    private String ptok;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("WebMeetingServer")
    private String webMeetingServer;
    @JsonProperty("ParticipantAnonymity")
    private boolean participantAnonymity;
    @JsonProperty("CustomAudioData")
    private String customAudioData;
    @JsonProperty("HubBrandId")
    private int hubBrandId;
    @JsonProperty("DeleteReason")
    private String deleteReason;
    @JsonProperty("DeletedDateTime")
    private String deletedDateTime;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 501242054)
    private transient MeetingRoomGetResultDao myDao;

    @Generated(hash = 1513642803)
    private transient String audioDetail__resolvedKey;

    @Generated(hash = 593646002)
    private transient String meetingRoomDetail__resolvedKey;

    @Generated(hash = 1516505193)
    private transient String meetingRoomUrls__resolvedKey;

    @JsonIgnore
    public MeetingRoomGetResult(String clientId, AudioDetail audioDetail, MeetingRoomDetail meetingRoomDetail, MeetingRoomUrls meetingRoomUrls) {
        this.clientId = clientId;
        this.audioDetail = audioDetail;
        this.meetingRoomDetail = meetingRoomDetail;
        this.meetingRoomUrls = meetingRoomUrls;

    }

    @Generated(hash = 2085667936)
    public MeetingRoomGetResult(Long id, String meetingRoomUrlsId, String audioDetailId, String meetingRoomDetailId, String correlationId,
                                int executionTime, String messageId, String serverDateTime, String attendeeStartPermission, String attendeeWebPassCode, String clientId,
                                int hubId, String hubName, int meetingRoomId, String presenterStartPermission, String ptok, String status, String webMeetingServer,
                                boolean participantAnonymity, String customAudioData, int hubBrandId, String deleteReason, String deletedDateTime) {
        this.id = id;
        this.meetingRoomUrlsId = meetingRoomUrlsId;
        this.audioDetailId = audioDetailId;
        this.meetingRoomDetailId = meetingRoomDetailId;
        this.correlationId = correlationId;
        this.executionTime = executionTime;
        this.messageId = messageId;
        this.serverDateTime = serverDateTime;
        this.attendeeStartPermission = attendeeStartPermission;
        this.attendeeWebPassCode = attendeeWebPassCode;
        this.clientId = clientId;
        this.hubId = hubId;
        this.hubName = hubName;
        this.meetingRoomId = meetingRoomId;
        this.presenterStartPermission = presenterStartPermission;
        this.ptok = ptok;
        this.status = status;
        this.webMeetingServer = webMeetingServer;
        this.participantAnonymity = participantAnonymity;
        this.customAudioData = customAudioData;
        this.hubBrandId = hubBrandId;
        this.deleteReason = deleteReason;
        this.deletedDateTime = deletedDateTime;
    }

    @Generated(hash = 1865160224)
    public MeetingRoomGetResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeetingRoomUrlsId() {
        return meetingRoomUrlsId;
    }

    public void setMeetingRoomUrlsId(String meetingRoomUrlsId) {
        this.meetingRoomUrlsId = meetingRoomUrlsId;
    }

    public String getAudioDetailId() {
        return audioDetailId;
    }

    public void setAudioDetailId(String audioDetailId) {
        this.audioDetailId = audioDetailId;
    }

    public String getMeetingRoomDetailId() {
        return meetingRoomDetailId;
    }

    public void setMeetingRoomDetailId(String meetingRoomDetailId) {
        this.meetingRoomDetailId = meetingRoomDetailId;
    }

    @JsonProperty("CorrelationId")
    public String getCorrelationId() {
        return correlationId;
    }

    @JsonProperty("CorrelationId")
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @Keep
    @JsonProperty("Errors")
    public List<Errors> getErrors() {
        return errors;
    }
    @Keep
    @JsonProperty("Errors")
    public void setErrors(List<Errors> errors) {
        this.errors = errors;
    }

    @JsonProperty("ExecutionTime")
    public int getExecutionTime() {
        return executionTime;
    }

    @JsonProperty("ExecutionTime")
    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    @JsonProperty("MessageId")
    public String getMessageId() {
        return messageId;
    }

    @JsonProperty("MessageId")
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @JsonProperty("ServerDateTime")
    public String getServerDateTime() {
        return serverDateTime;
    }

    @JsonProperty("ServerDateTime")
    public void setServerDateTime(String serverDateTime) {
        this.serverDateTime = serverDateTime;
    }

    @JsonProperty("AttendeeStartPermission")
    public String getAttendeeStartPermission() {
        return attendeeStartPermission;
    }

    @JsonProperty("AttendeeStartPermission")
    public void setAttendeeStartPermission(String attendeeStartPermission) {
        this.attendeeStartPermission = attendeeStartPermission;
    }

    @JsonProperty("AttendeeWebPassCode")
    public String getAttendeeWebPassCode() {
        return attendeeWebPassCode;
    }

    @JsonProperty("AttendeeWebPassCode")
    public void setAttendeeWebPassCode(String attendeeWebPassCode) {
        this.attendeeWebPassCode = attendeeWebPassCode;
    }

    @Keep
    @JsonProperty("Attendees")
    public List<Attendee> getAttendees() {
        return attendees;
    }

    @Keep
    @JsonProperty("Attendees")
    public void setAttendees(List<Attendee> attendees) {
        this.attendees = attendees;
    }

    @Keep
    @JsonProperty("AudioDetail")
    public AudioDetail getAudioDetail() {
        return audioDetail;
    }

    @Keep
    @JsonProperty("AudioDetail")
    public void setAudioDetail(AudioDetail audioDetail) {
        this.audioDetail = audioDetail;
    }

    @JsonProperty("ClientId")
    public String getClientId() {
        return clientId;
    }

    @JsonProperty("ClientId")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Keep
    @JsonProperty("ConferenceOptions")
    public List<ConferenceOption> getConferenceOptions() {
        return conferenceOptions;
    }

    @Keep
    @JsonProperty("ConferenceOptions")
    public void setConferenceOptions(List<ConferenceOption> conferenceOptions) {
        this.conferenceOptions = conferenceOptions;
    }

    @JsonProperty("HubId")
    public int getHubId() {
        return hubId;
    }

    @JsonProperty("HubId")
    public void setHubId(int hubId) {
        this.hubId = hubId;
    }

    @JsonProperty("HubName")
    public String getHubName() {
        return hubName;
    }

    @JsonProperty("HubName")
    public void setHubName(String hubName) {
        this.hubName = hubName;
    }

    @Keep
    @JsonProperty("MeetingRoomDetail")
    public MeetingRoomDetail getMeetingRoomDetail() {
        return meetingRoomDetail;
    }

    @Keep
    @JsonProperty("MeetingRoomDetail")
    public void setMeetingRoomDetail(MeetingRoomDetail meetingRoomDetail) {
        this.meetingRoomDetail = meetingRoomDetail;
    }

    @JsonProperty("MeetingRoomId")
    public int getMeetingRoomId() {
        return meetingRoomId;
    }

    @JsonProperty("MeetingRoomId")
    public void setMeetingRoomId(int meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }

    @Keep
    @JsonProperty("MeetingRoomUrls")
    public MeetingRoomUrls getMeetingRoomUrls() {
        return meetingRoomUrls;
    }

    @Keep
    @JsonProperty("MeetingRoomUrls")
    public void setMeetingRoomUrls(MeetingRoomUrls meetingRoomUrls) {
        this.meetingRoomUrls = meetingRoomUrls;
    }

    @Keep
    @JsonProperty("Packages")
    public List<Package> getPackages() {
        return packages;
    }

    @Keep
    @JsonProperty("Packages")
    public void setPackages(List<Package> packages) {
        this.packages = packages;
    }

    @JsonProperty("PresenterStartPermission")
    public String getPresenterStartPermission() {
        return presenterStartPermission;
    }

    @JsonProperty("PresenterStartPermission")
    public void setPresenterStartPermission(String presenterStartPermission) {
        this.presenterStartPermission = presenterStartPermission;
    }

    @Keep
    @JsonProperty("Presenters")
    public List<Presenter> getPresenters() {
        return presenters;
    }

    @Keep
    @JsonProperty("Presenters")
    public void setPresenters(List<Presenter> presenters) {
        this.presenters = presenters;
    }

    @JsonProperty("Ptok")
    public String getPtok() {
        return ptok;
    }

    @JsonProperty("Ptok")
    public void setPtok(String ptok) {
        this.ptok = ptok;
    }

    @JsonProperty("Status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("Status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("WebMeetingServer")
    public String getWebMeetingServer() {
        return webMeetingServer;
    }

    @JsonProperty("WebMeetingServer")
    public void setWebMeetingServer(String webMeetingServer) {
        this.webMeetingServer = webMeetingServer;
    }

    @JsonProperty("ParticipantAnonymity")
    public boolean isParticipantAnonymity() {
        return participantAnonymity;
    }

    @JsonProperty("ParticipantAnonymity")
    public void setParticipantAnonymity(boolean participantAnonymity) {
        this.participantAnonymity = participantAnonymity;
    }

    @JsonProperty("CustomAudioData")
    public String getCustomAudioData() {
        return customAudioData;
    }

    @JsonProperty("CustomAudioData")
    public void setCustomAudioData(String customAudioData) {
        this.customAudioData = customAudioData;
    }

    @JsonProperty("HubBrandId")
    public int getHubBrandId() {
        return hubBrandId;
    }

    @JsonProperty("HubBrandId")
    public void setHubBrandId(int hubBrandId) {
        this.hubBrandId = hubBrandId;
    }

    @JsonProperty("DeleteReason")
    public String getDeleteReason() {
        return deleteReason;
    }

    @JsonProperty("DeleteReason")
    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }

    @JsonProperty("DeletedDateTime")
    public String getDeletedDateTime() {
        return deletedDateTime;
    }

    @JsonProperty("DeletedDateTime")
    public void setDeletedDateTime(String deletedDateTime) {
        this.deletedDateTime = deletedDateTime;
    }

    public boolean getParticipantAnonymity() {
        return participantAnonymity;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1999512582)
    public synchronized void resetErrors() {
        errors = null;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 837807367)
    public synchronized void resetAttendees() {
        attendees = null;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1958637009)
    public synchronized void resetConferenceOptions() {
        conferenceOptions = null;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1363477572)
    public synchronized void resetPackages() {
        packages = null;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 283074147)
    public synchronized void resetPresenters() {
        presenters = null;
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
    @Generated(hash = 1829676474)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMeetingRoomGetResultDao() : null;
    }

}
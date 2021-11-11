package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
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
    "AudioOnlyConferences",
    "ClientDetails",
    "CompanyDetails",
    "ExcessiveConfs",
    "MeetingRooms",
    "AudioConferenceCount",
    "HasGMWebinar",
    "MeetingRoomsCount"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class ClientInfoResult {

    @Id(autoincrement = true)
    @JsonProperty
    private Long id;

    @JsonProperty
    @Index
    private String clientDetailsId;
    @JsonProperty
    @Index
    private String companyDetailsId;

    @JsonProperty("CorrelationId")
    private String correlationId;

    @ToMany(referencedJoinProperty = "errorsId" )
    @JsonProperty("Errors")
    private List<Error> errors = null;

    @JsonProperty("ExecutionTime")
    private int executionTime;
    @JsonProperty("MessageId")
    private String messageId;
    @JsonProperty("ServerDateTime")
    private String serverDateTime;

    @ToMany(referencedJoinProperty = "audioOnlyConferencesId")
    @JsonProperty("AudioOnlyConferences")
    private List<AudioDetail> audioOnlyConferences = null;

    @ToOne(joinProperty = "clientDetailsId")
    @JsonProperty("ClientDetails")
    private ClientDetails clientDetails;

    @ToOne(joinProperty = "companyDetailsId")
    @JsonProperty("CompanyDetails")
    private CompanyDetails companyDetails;

    @Convert(converter = GreenConverter.class, columnType = String.class)
    @JsonProperty("ExcessiveConfs")
    private List<String> excessiveConfs = null;

    @ToMany(referencedJoinProperty = "meetingRoomsId")
    @JsonProperty("MeetingRooms")
    private List<MeetingRoom> meetingRooms = null;

    @JsonProperty("AudioConferenceCount")
    private int audioConferenceCount;

    @JsonProperty("HasGMWebinar")
    private boolean hasGMWebinar;
    @JsonProperty("MeetingRoomsCount")
    private int meetingRoomsCount;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1042205838)
    private transient ClientInfoResultDao myDao;

    @Generated(hash = 793059553)
    private transient String clientDetails__resolvedKey;

    @Generated(hash = 428495080)
    private transient String companyDetails__resolvedKey;

    public ClientInfoResult() {
    }

    @Generated(hash = 99982617)
    public ClientInfoResult(Long id, String clientDetailsId, String companyDetailsId,
                            String correlationId, int executionTime, String messageId, String serverDateTime,
                            List<String> excessiveConfs, int audioConferenceCount, boolean hasGMWebinar,
                            int meetingRoomsCount) {
        this.id = id;
        this.clientDetailsId = clientDetailsId;
        this.companyDetailsId = companyDetailsId;
        this.correlationId = correlationId;
        this.executionTime = executionTime;
        this.messageId = messageId;
        this.serverDateTime = serverDateTime;
        this.excessiveConfs = excessiveConfs;
        this.audioConferenceCount = audioConferenceCount;
        this.hasGMWebinar = hasGMWebinar;
        this.meetingRoomsCount = meetingRoomsCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientDetailsId() {
        return clientDetailsId;
    }

    public void setClientDetailsId(String clientDetailsId) {
        this.clientDetailsId = clientDetailsId;
    }

    public String getCompanyDetailsId() {
        return companyDetailsId;
    }

    public void setCompanyDetailsId(String companyDetailsId) {
        this.companyDetailsId = companyDetailsId;
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
    public List<Error> getErrors() {
        return errors;
    }

    @Keep
    @JsonProperty("Errors")
    public void setErrors(List<Error> errors) {
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

    @Keep
    @JsonProperty("AudioOnlyConferences")
    public List<AudioDetail> getAudioOnlyConferences() {
        return audioOnlyConferences;
    }

    @JsonProperty("AudioOnlyConferences")
    public void setAudioOnlyConferences(List<AudioDetail> audioOnlyConferences) {
        this.audioOnlyConferences = audioOnlyConferences;
    }

    @Keep
    @JsonProperty("ClientDetails")
    public ClientDetails getClientDetails() {
        return clientDetails;
    }

    @Keep
    @JsonProperty("ClientDetails")
    public void setClientDetails(ClientDetails clientDetails) {
        this.clientDetails = clientDetails;
    }

    @Keep
    @JsonProperty("CompanyDetails")
    public CompanyDetails getCompanyDetails() {
        return companyDetails;
    }

    @Keep
    @JsonProperty("CompanyDetails")
    public void setCompanyDetails(CompanyDetails companyDetails) {
        this.companyDetails = companyDetails;
    }

    @JsonProperty("ExcessiveConfs")
    public List<String> getExcessiveConfs() {
        return excessiveConfs;
    }

    @JsonProperty("ExcessiveConfs")
    public void setExcessiveConfs(List<String> excessiveConfs) {
        this.excessiveConfs = excessiveConfs;
    }

    @Keep
    @JsonProperty("MeetingRooms")
    public List<MeetingRoom> getMeetingRooms() {
        return meetingRooms;
    }

    @Keep
    @JsonProperty("MeetingRooms")
    public void setMeetingRooms(List<MeetingRoom> meetingRooms) {
        this.meetingRooms = meetingRooms;
    }

    @JsonProperty("AudioConferenceCount")
    public int getAudioConferenceCount() {
        return audioConferenceCount;
    }

    @JsonProperty("AudioConferenceCount")
    public void setAudioConferenceCount(int audioConferenceCount) {
        this.audioConferenceCount = audioConferenceCount;
    }

    @JsonProperty("HasGMWebinar")
    public boolean isHasGMWebinar() {
        return hasGMWebinar;
    }

    @JsonProperty("HasGMWebinar")
    public void setHasGMWebinar(boolean hasGMWebinar) {
        this.hasGMWebinar = hasGMWebinar;
    }

    @JsonProperty("MeetingRoomsCount")
    public int getMeetingRoomsCount() {
        return meetingRoomsCount;
    }

    @JsonProperty("MeetingRoomsCount")
    public void setMeetingRoomsCount(int meetingRoomsCount) {
        this.meetingRoomsCount = meetingRoomsCount;
    }

    public boolean getHasGMWebinar() {
        return this.hasGMWebinar;
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

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1999512582)
    public synchronized void resetErrors() {
        errors = null;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1173995407)
    public synchronized void resetAudioOnlyConferences() {
        audioOnlyConferences = null;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1764639239)
    public synchronized void resetMeetingRooms() {
        meetingRooms = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1023908886)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getClientInfoResultDao() : null;
    }

}
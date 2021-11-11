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
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Availability",
    "ConfId",
    "ConferenceName",
    "Deleted",
    "HashWebApiAuth",
    "ListenPassCode",
    "MaxParticipants",
    "MeetingRoomId",
    "MeetingRoomOptions",
    "MeetingRoomUrls",
    "ModeratorEmail",
    "ModeratorPassCode",
    "ModeratorSecurityCode",
    "ParticipantPassCode",
    "Ptok",
    "RoomName",
    "SecurityCode",
    "Status",
    "TimeZone",
    "WebMeetingServer",
    "bridgeOptions",
    "phoneInformation",
    "reservationOptions",
    "ParticipantAnonymity",
    "PrimaryAccessNumber",
    "UseHtml5"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class MeetingRoom {

    @Id(autoincrement = true)
    @JsonProperty
    private Long id;

    private String meetingRoomsId;
    private String meetingRoomUrlsId;

    @JsonProperty("Availability")
    private String availability;
    @JsonProperty("ConfId")
    private String confId;
    @JsonProperty("ConferenceName")
    private String conferenceName;
    @JsonProperty("Deleted")
    private boolean deleted;
    @JsonProperty("HashWebApiAuth")
    private String hashWebApiAuth;
    @JsonProperty("ListenPassCode")
    private String listenPassCode;
    @JsonProperty("MaxParticipants")
    private String maxParticipants;
    @JsonProperty("MeetingRoomId")
    private String meetingRoomId;

    @ToMany(referencedJoinProperty = "meetingRoomOptionsId")
    @JsonProperty("MeetingRoomOptions")
    private List<MeetingRoomOption> meetingRoomOptions = null;

    @ToOne(joinProperty = "meetingRoomUrlsId")
    @JsonProperty("MeetingRoomUrls")
    private MeetingRoomUrls meetingRoomUrls;

    @JsonProperty("ModeratorEmail")
    private String moderatorEmail;
    @JsonProperty("ModeratorPassCode")
    private String moderatorPassCode;
    @JsonProperty("ModeratorSecurityCode")
    private String moderatorSecurityCode;
    @JsonProperty("ParticipantPassCode")
    private String participantPassCode;
    @JsonProperty("Ptok")
    private String ptok;
    @JsonProperty("RoomName")
    private String roomName;
    @JsonProperty("SecurityCode")
    private String securityCode;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("TimeZone")
    private String timeZone;
    @JsonProperty("WebMeetingServer")
    private String webMeetingServer;


    @Convert(converter = GreenConverter.class, columnType = String.class)
    @JsonProperty("bridgeOptions")
    private List<String> bridgeOptions = null;

    @ToMany(referencedJoinProperty = "phoneInformationId")
    @JsonProperty("phoneInformation")
    private List<PhoneInformation> phoneInformation = null;

    @Convert(converter = GreenConverter.class, columnType = String.class)
    @JsonProperty("reservationOptions")
    private List<String> reservationOptions = null;

    @JsonProperty("ParticipantAnonymity")
    private boolean participantAnonymity;

    @JsonProperty("PrimaryAccessNumber")
    private String primaryAccessNumber;

    @JsonProperty("UseHtml5")
    private boolean useHtml5;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2114634539)
    private transient MeetingRoomDao myDao;

    @Generated(hash = 1729557487)
    public MeetingRoom(Long id, String meetingRoomsId, String meetingRoomUrlsId, String availability,
                       String confId, String conferenceName, boolean deleted, String hashWebApiAuth,
                       String listenPassCode, String maxParticipants, String meetingRoomId, String moderatorEmail,
                       String moderatorPassCode, String moderatorSecurityCode, String participantPassCode,
                       String ptok, String roomName, String securityCode, String status, String timeZone,
                       String webMeetingServer, List<String> bridgeOptions, List<String> reservationOptions,
                       boolean participantAnonymity, String primaryAccessNumber, boolean useHtml5) {
        this.id = id;
        this.meetingRoomsId = meetingRoomsId;
        this.meetingRoomUrlsId = meetingRoomUrlsId;
        this.availability = availability;
        this.confId = confId;
        this.conferenceName = conferenceName;
        this.deleted = deleted;
        this.hashWebApiAuth = hashWebApiAuth;
        this.listenPassCode = listenPassCode;
        this.maxParticipants = maxParticipants;
        this.meetingRoomId = meetingRoomId;
        this.moderatorEmail = moderatorEmail;
        this.moderatorPassCode = moderatorPassCode;
        this.moderatorSecurityCode = moderatorSecurityCode;
        this.participantPassCode = participantPassCode;
        this.ptok = ptok;
        this.roomName = roomName;
        this.securityCode = securityCode;
        this.status = status;
        this.timeZone = timeZone;
        this.webMeetingServer = webMeetingServer;
        this.bridgeOptions = bridgeOptions;
        this.reservationOptions = reservationOptions;
        this.participantAnonymity = participantAnonymity;
        this.primaryAccessNumber = primaryAccessNumber;
        this.useHtml5 = useHtml5;
    }

    @Generated(hash = 1099319091)
    public MeetingRoom() {
    }

    @Generated(hash = 1516505193)
    private transient String meetingRoomUrls__resolvedKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeetingRoomsId() {
        return meetingRoomsId;
    }

    public void setMeetingRoomsId(String meetingRoomsId) {
        this.meetingRoomsId = meetingRoomsId;
    }

    public String getMeetingRoomUrlsId() {
        return meetingRoomUrlsId;
    }

    public void setMeetingRoomUrlsId(String meetingRoomUrlsId) {
        this.meetingRoomUrlsId = meetingRoomUrlsId;
    }

    @JsonProperty("Availability")
    public String getAvailability() {
        return availability;
    }

    @JsonProperty("Availability")
    public void setAvailability(String availability) {
        this.availability = availability;
    }

    @JsonProperty("ConfId")
    public String getConfId() {
        return confId;
    }

    @JsonProperty("ConfId")
    public void setConfId(String confId) {
        this.confId = confId;
    }

    @JsonProperty("ConferenceName")
    public String getConferenceName() {
        return conferenceName;
    }

    @JsonProperty("ConferenceName")
    public void setConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
    }

    @JsonProperty("Deleted")
    public boolean isDeleted() {
        return deleted;
    }

    @JsonProperty("Deleted")
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @JsonProperty("HashWebApiAuth")
    public String getHashWebApiAuth() {
        return hashWebApiAuth;
    }

    @JsonProperty("HashWebApiAuth")
    public void setHashWebApiAuth(String hashWebApiAuth) {
        this.hashWebApiAuth = hashWebApiAuth;
    }

    @JsonProperty("ListenPassCode")
    public String getListenPassCode() {
        return listenPassCode;
    }

    @JsonProperty("ListenPassCode")
    public void setListenPassCode(String listenPassCode) {
        this.listenPassCode = listenPassCode;
    }

    @JsonProperty("MaxParticipants")
    public String getMaxParticipants() {
        return maxParticipants;
    }

    @JsonProperty("MaxParticipants")
    public void setMaxParticipants(String maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    @JsonProperty("MeetingRoomId")
    public String getMeetingRoomId() {
        return meetingRoomId;
    }

    @JsonProperty("MeetingRoomId")
    public void setMeetingRoomId(String meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
    }

    @Keep
    @JsonProperty("MeetingRoomOptions")
    public List<MeetingRoomOption> getMeetingRoomOptions() {
        return meetingRoomOptions;
    }

    @Keep
    @JsonProperty("MeetingRoomOptions")
    public void setMeetingRoomOptions(List<MeetingRoomOption> meetingRoomOptions) {
        this.meetingRoomOptions = meetingRoomOptions;
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

    @JsonProperty("ModeratorEmail")
    public String getModeratorEmail() {
        return moderatorEmail;
    }

    @JsonProperty("ModeratorEmail")
    public void setModeratorEmail(String moderatorEmail) {
        this.moderatorEmail = moderatorEmail;
    }

    @JsonProperty("ModeratorPassCode")
    public String getModeratorPassCode() {
        return moderatorPassCode;
    }

    @JsonProperty("ModeratorPassCode")
    public void setModeratorPassCode(String moderatorPassCode) {
        this.moderatorPassCode = moderatorPassCode;
    }

    @JsonProperty("ModeratorSecurityCode")
    public String getModeratorSecurityCode() {
        return moderatorSecurityCode;
    }

    @JsonProperty("ModeratorSecurityCode")
    public void setModeratorSecurityCode(String moderatorSecurityCode) {
        this.moderatorSecurityCode = moderatorSecurityCode;
    }

    @JsonProperty("ParticipantPassCode")
    public String getParticipantPassCode() {
        return participantPassCode;
    }

    @JsonProperty("ParticipantPassCode")
    public void setParticipantPassCode(String participantPassCode) {
        this.participantPassCode = participantPassCode;
    }

    @JsonProperty("Ptok")
    public String getPtok() {
        return ptok;
    }

    @JsonProperty("Ptok")
    public void setPtok(String ptok) {
        this.ptok = ptok;
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

    @JsonProperty("Status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("Status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("TimeZone")
    public String getTimeZone() {
        return timeZone;
    }

    @JsonProperty("TimeZone")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @JsonProperty("WebMeetingServer")
    public String getWebMeetingServer() {
        return webMeetingServer;
    }

    @JsonProperty("WebMeetingServer")
    public void setWebMeetingServer(String webMeetingServer) {
        this.webMeetingServer = webMeetingServer;
    }

    @JsonProperty("bridgeOptions")
    public List<String> getBridgeOptions() {
        return bridgeOptions;
    }

    @JsonProperty("bridgeOptions")
    public void setBridgeOptions(List<String> bridgeOptions) {
        this.bridgeOptions = bridgeOptions;
    }

    @Keep
    @JsonProperty("phoneInformation")
    public List<PhoneInformation> getPhoneInformation() {
        return phoneInformation;
    }

    @Keep
    @JsonProperty("phoneInformation")
    public void setPhoneInformation(List<PhoneInformation> phoneInformation) {
        this.phoneInformation = phoneInformation;
    }

    @Keep
    @JsonProperty("reservationOptions")
    public List<String> getReservationOptions() {
        return reservationOptions;
    }

    @Keep
    @JsonProperty("reservationOptions")
    public void setReservationOptions(List<String> reservationOptions) {
        this.reservationOptions = reservationOptions;
    }

    @JsonProperty("ParticipantAnonymity")
    public boolean isParticipantAnonymity() {
        return participantAnonymity;
    }

    @JsonProperty("ParticipantAnonymity")
    public void setParticipantAnonymity(boolean participantAnonymity) {
        this.participantAnonymity = participantAnonymity;
    }

    public boolean getDeleted() {
        return this.deleted;
    }

    public boolean getParticipantAnonymity() {
        return this.participantAnonymity;
    }


    @JsonProperty("PrimaryAccessNumber")
    public String getPrimaryAccessNumber() {
        return primaryAccessNumber;
    }

    @JsonProperty("PrimaryAccessNumber")
    public void setPrimaryAccessNumber(String primaryAccessNumber) {
        this.primaryAccessNumber = primaryAccessNumber;
    }

    @JsonProperty("UseHtml5")
    public boolean isUseHtml5() {
        return useHtml5;
    }

    @JsonProperty("UseHtml5")
    public void setUseHtml5(boolean useHtml5) {
        this.useHtml5 = useHtml5;
    }

    public boolean getUseHtml5() {
        return this.useHtml5;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1390441585)
    public synchronized void resetMeetingRoomOptions() {
        meetingRoomOptions = null;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 927028358)
    public synchronized void resetPhoneInformation() {
        phoneInformation = null;
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

    @Override
    public String toString() {
        return conferenceName;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1089276473)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMeetingRoomDao() : null;
    }
}
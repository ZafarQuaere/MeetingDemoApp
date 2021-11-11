package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

import java.util.List;

/**
 * Created by nnennaiheke on 8/3/17.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ConfId",
    "ConferenceName",
    "ListenPassCode",
    "ModeratorPassCode",
    "ParticipantPassCode",
    "bridgeOptions",
    "phoneInformation"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class AudioDetail {
    @JsonIgnore
    @Id(autoincrement = true)
    private Long id;
    private String audioDetailId;
    private String audioOnlyConferencesId;

    @JsonProperty("ConfId")
    private String confId;
    @JsonProperty("ConferenceName")
    private String conferenceName;
    @JsonProperty("ListenPassCode")
    private String listenPassCode;
    @JsonProperty("ModeratorPassCode")
    private String moderatorPassCode;
    @JsonProperty("ParticipantPassCode")
    private String participantPassCode;

    @Convert(converter = GreenConverter.class, columnType = String.class)
    @JsonProperty("bridgeOptions")
    private List<String> bridgeOptions = null;

    @ToMany(referencedJoinProperty = "phoneInformationId")
    @JsonProperty("phoneInformation")
    private List<PhoneInformation> phoneInformation = null;

    @Convert(converter = GreenConverter.class, columnType = String.class)
    @JsonProperty("reservationOptions")
    private List<String> reservationOptions = null;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 300503791)
    private transient AudioDetailDao myDao;

    public AudioDetail() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAudioDetailId() {
        return audioDetailId;
    }

    public void setAudioDetailId(String audioDetailId) {
        this.audioDetailId = audioDetailId;
    }



    @Generated(hash = 1344426113)
    public AudioDetail(Long id, String audioDetailId, String audioOnlyConferencesId, String confId,
                       String conferenceName, String listenPassCode, String moderatorPassCode,
                       String participantPassCode, List<String> bridgeOptions, List<String> reservationOptions) {
        this.id = id;
        this.audioDetailId = audioDetailId;
        this.audioOnlyConferencesId = audioOnlyConferencesId;
        this.confId = confId;
        this.conferenceName = conferenceName;
        this.listenPassCode = listenPassCode;
        this.moderatorPassCode = moderatorPassCode;
        this.participantPassCode = participantPassCode;
        this.bridgeOptions = bridgeOptions;
        this.reservationOptions = reservationOptions;
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
    public String  getConferenceName() {
        return conferenceName;
    }

    @JsonProperty("ConferenceName")
    public void setConferenceName(String  conferenceName) {
        this.conferenceName = conferenceName;
    }

    @JsonProperty("ListenPassCode")
    public String getListenPassCode() {
        return listenPassCode;
    }

    @JsonProperty("ListenPassCode")
    public void setListenPassCode(String listenPassCode) {
        this.listenPassCode = listenPassCode;
    }

    @JsonProperty("ModeratorPassCode")
    public String getModeratorPassCode() {
        return moderatorPassCode;
    }

    @JsonProperty("ModeratorPassCode")
    public void setModeratorPassCode(String moderatorPassCode) {
        this.moderatorPassCode = moderatorPassCode;
    }

    @JsonProperty("ParticipantPassCode")
    public String getParticipantPassCode() {
        return participantPassCode;
    }

    @JsonProperty("ParticipantPassCode")
    public void setParticipantPassCode(String participantPassCode) {
        this.participantPassCode = participantPassCode;
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

    public String getAudioOnlyConferencesId() {
        return this.audioOnlyConferencesId;
    }

    public void setAudioOnlyConferencesId(String audioOnlyConferencesId) {
        this.audioOnlyConferencesId = audioOnlyConferencesId;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 278828666)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAudioDetailDao() : null;
    }

}
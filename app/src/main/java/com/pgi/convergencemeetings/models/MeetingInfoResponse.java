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
import org.greenrobot.greendao.annotation.ToOne;


/**
 * Created by nnennaiheke on 8/3/17.
 */



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "MeetingRoomGetResult"
})


@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class MeetingInfoResponse extends BaseModel {

    @Id(autoincrement = true)
    @JsonIgnore
    private Long id;
    @JsonIgnore
    private long MeetingRoomGetResultId;


    //    @JsonIgnore
    @ToOne(joinProperty = "MeetingRoomGetResultId")
    @JsonProperty("MeetingRoomGetResult")
    private MeetingRoomGetResult meetingRoomGetResult;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 377498632)
    private transient MeetingInfoResponseDao myDao;

    @Generated(hash = 784520778)
    public MeetingInfoResponse(Long id, long MeetingRoomGetResultId) {
        this.id = id;
        this.MeetingRoomGetResultId = MeetingRoomGetResultId;
    }

    @Generated(hash = 224985594)
    public MeetingInfoResponse() {
    }

    @Generated(hash = 996684633)
    private transient Long meetingRoomGetResult__resolvedKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getMeetingRoomGetResultId() {
        return MeetingRoomGetResultId;
    }


    public void setMeetingRoomGetResultId(long meetingRoomGetResultId) {
        MeetingRoomGetResultId = meetingRoomGetResultId;
    }

    @Keep
    public MeetingRoomGetResult getMeetingRoomGetResult() {
        return meetingRoomGetResult;
    }

    @Keep
    public void setMeetingRoomGetResult(MeetingRoomGetResult meetingRoomGetResult) {
        this.meetingRoomGetResult = meetingRoomGetResult;
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
    @Generated(hash = 2084893427)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMeetingInfoResponseDao() : null;
    }

}
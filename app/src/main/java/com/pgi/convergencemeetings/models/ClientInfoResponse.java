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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ClientInfoResult"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class ClientInfoResponse extends BaseModel {

    @Id(autoincrement = true)
    @JsonIgnore
    private Long id;

    @JsonIgnore
    private String clientInfoResultId;

    @ToOne(joinProperty = "clientInfoResultId")
    @JsonProperty("ClientInfoResult")
    private ClientInfoResult clientInfoResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientInfoResultId() {
        return clientInfoResultId;
    }

    public void setClientInfoResultId(String clientInfoResultId) {
        this.clientInfoResultId = clientInfoResultId;
    }

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 181860442)
    private transient ClientInfoResponseDao myDao;

    @Generated(hash = 438679550)
    public ClientInfoResponse(Long id, String clientInfoResultId) {
        this.id = id;
        this.clientInfoResultId = clientInfoResultId;
    }

    @Generated(hash = 1610408606)
    public ClientInfoResponse() {
    }

    @Generated(hash = 262085861)
    private transient String clientInfoResult__resolvedKey;

    @Keep
    public ClientInfoResult getClientInfoResult() {
        return clientInfoResult;
    }

    @Keep
    public void setClientInfoResult(ClientInfoResult clientInfoResult) {
        this.clientInfoResult = clientInfoResult;
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
    @Generated(hash = 52236739)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getClientInfoResponseDao() : null;
    }



}
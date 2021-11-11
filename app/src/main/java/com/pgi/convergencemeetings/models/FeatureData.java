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
 * Created by nnennaiheke on 8/4/17.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Features"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class FeatureData {
    @JsonIgnore
    @Id(autoincrement = true)
    private Long id;

    private String featureDataId;

    @ToMany(referencedJoinProperty = "featuresId")
    @JsonProperty("Features")
    private List<Feature> features = null;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1763984750)
    private transient FeatureDataDao myDao;

    public FeatureData() {

    }

    public Long getId() {
        return id;
    }

    @Generated(hash = 442684093)
    public FeatureData(Long id, String featureDataId) {
        this.id = id;
        this.featureDataId = featureDataId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeatureDataId() {
        return featureDataId;
    }

    public void setFeatureDataId(String featureDataId) {
        this.featureDataId = featureDataId;
    }

    @Keep
    public List<Feature> getFeatures() {
        return features;
    }

    @Keep
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1753751471)
    public synchronized void resetFeatures() {
        features = null;
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
    @Generated(hash = 1255441020)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getFeatureDataDao() : null;
    }
}
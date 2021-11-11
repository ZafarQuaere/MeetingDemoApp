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
 * Created by nnennaiheke on 8/4/17.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Description",
    "FeatureData",
    "Name",
    "ProductCode",
    "HasAudioConf",
    "HasWebConf",
    "HasWebinar"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Product {
    @JsonIgnore
    @Id(autoincrement = true)
    private Long id;

    private String productsId;

    private String featureDataId;

    @JsonProperty("Description")
    private String description;

    @ToOne(joinProperty = "featureDataId")
    @JsonProperty("FeatureData")
    private FeatureData featureData;

    @JsonProperty("Name")
    private String name;
    @JsonProperty("ProductCode")
    private String productCode;
    @JsonProperty("HasAudioConf")
    private boolean hasAudioConf;
    @JsonProperty("HasWebConf")
    private boolean hasWebConf;
    @JsonProperty("HasWebinar")
    private boolean hasWebinar;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 694336451)
    private transient ProductDao myDao;

    @Generated(hash = 575836869)
    public Product(Long id, String productsId, String featureDataId, String description,
                   String name, String productCode, boolean hasAudioConf, boolean hasWebConf,
                   boolean hasWebinar) {
        this.id = id;
        this.productsId = productsId;
        this.featureDataId = featureDataId;
        this.description = description;
        this.name = name;
        this.productCode = productCode;
        this.hasAudioConf = hasAudioConf;
        this.hasWebConf = hasWebConf;
        this.hasWebinar = hasWebinar;
    }

    @Generated(hash = 1890278724)
    public Product() {
    }

    @Generated(hash = 1622137991)
    private transient String featureData__resolvedKey;

    public Long getId() {
        return id;
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

    public String getProductsId() {
        return productsId;
    }

    public void setProductsId(String productsId) {
        this.productsId = productsId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Keep
    public FeatureData getFeatureData() {
        return featureData;
    }

    @Keep
    public void setFeatureData(FeatureData featureData) {
        this.featureData = featureData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public boolean getHasAudioConf() {
        return hasAudioConf;
    }

    public void setHasAudioConf(boolean hasAudioConf) {
        this.hasAudioConf = hasAudioConf;
    }

    public boolean getHasWebConf() {
        return hasWebConf;
    }

    public void setHasWebConf(boolean hasWebConf) {
        this.hasWebConf = hasWebConf;
    }

    public boolean getHasWebinar() {
        return hasWebinar;
    }

    public void setHasWebinar(boolean hasWebinar) {
        this.hasWebinar = hasWebinar;
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
    @Generated(hash = 1171535257)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getProductDao() : null;
    }

}
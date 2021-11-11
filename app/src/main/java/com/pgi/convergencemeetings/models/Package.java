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
    "Description",
    "Name",
    "PackageCode",
    "ProductData"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Package {
    @JsonIgnore
    @Id(autoincrement = true)
    private Long id;

    private String packagesId;
    private String productDataId;

    @JsonProperty("Description")
    private String description;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("PackageCode")
    private String packageCode;

    @ToOne(joinProperty = "productDataId")
    @JsonProperty("ProductData")
    private ProductData productData;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 40750146)
    private transient PackageDao myDao;


    @Generated(hash = 1485031828)
    public Package(Long id, String packagesId, String productDataId, String description,
                   String name, String packageCode) {
        this.id = id;
        this.packagesId = packagesId;
        this.productDataId = productDataId;
        this.description = description;
        this.name = name;
        this.packageCode = packageCode;
    }

    @Generated(hash = 1194681198)
    public Package() {
    }

    @Generated(hash = 1908611872)
    private transient String productData__resolvedKey;


    public String getProductDataId() {
        return productDataId;
    }

    public void setProductDataId(String productDataId) {
        this.productDataId = productDataId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackagesId() {
        return packagesId;
    }

    public void setPackagesId(String packagesId) {
        this.packagesId = packagesId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    @Keep
    public ProductData getProductData() {
        return productData;
    }

    @Keep
    public void setProductData(ProductData productData) {
        this.productData = productData;
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
    @Generated(hash = 943005894)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPackageDao() : null;
    }

}
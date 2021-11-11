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
import org.greenrobot.greendao.annotation.ToOne;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Active",
    "BillToId",
    "ClientContactDetails",
    "ClientId",
    "CreditCardDetail",
    "DefaultLanguage",
    "Hold",
    "OperationComments",
    "Password",
    "PasswordCanBeModified",
    "PromotionCodes",
    "SoftPhoneAutoConnect",
    "SoftPhoneEnable",
    "SpecialInfo",
    "SubscriptionId",
    "TerritoryCode",
    "ProfileImageUrl"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class ClientDetails {

    @Id(autoincrement = true)
    @JsonProperty
    private Long id;

    @JsonProperty
    @Index
    private String clientContactDetailsId;

    @JsonProperty("Active")
    private boolean active;
    @JsonProperty("BillToId")
    private int billToId;

    @ToOne(joinProperty = "clientContactDetailsId")
    @JsonProperty("ClientContactDetails")
    private ClientContactDetails clientContactDetails;

    @JsonProperty("ClientId")
    private String clientId;
    @JsonProperty("CreditCardDetail")
    private String creditCardDetail;
    @JsonProperty("DefaultLanguage")
    private String defaultLanguage;
    @JsonProperty("Hold")
    private boolean hold;
    @JsonProperty("OperationComments")
    private String operationComments;
    @JsonProperty("Password")
    private String password;
    @JsonProperty("PasswordCanBeModified")
    private boolean passwordCanBeModified;

    @Convert(converter = GreenConverter.class, columnType = String.class)
    @JsonProperty("PromotionCodes")
    private List<String> promotionCodes = null;

    @JsonProperty("SoftPhoneAutoConnect")
    private boolean softPhoneAutoConnect;
    @JsonProperty("SoftPhoneEnable")
    private boolean softPhoneEnable;
    @JsonProperty("SpecialInfo")
    private String specialInfo;
    @JsonProperty("SubscriptionId")
    private String subscriptionId;
    @JsonProperty("TerritoryCode")
    private String territoryCode;
    @JsonProperty("ProfileImageUrl")
    private String profileImageUrl;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 290046085)
    private transient ClientDetailsDao myDao;

    @Generated(hash = 606383759)
    public ClientDetails(Long id, String clientContactDetailsId, boolean active, int billToId,
                         String clientId, String creditCardDetail, String defaultLanguage, boolean hold,
                         String operationComments, String password, boolean passwordCanBeModified,
                         List<String> promotionCodes, boolean softPhoneAutoConnect,
                         boolean softPhoneEnable, String specialInfo, String subscriptionId,
                         String territoryCode, String profileImageUrl) {
        this.id = id;
        this.clientContactDetailsId = clientContactDetailsId;
        this.active = active;
        this.billToId = billToId;
        this.clientId = clientId;
        this.creditCardDetail = creditCardDetail;
        this.defaultLanguage = defaultLanguage;
        this.hold = hold;
        this.operationComments = operationComments;
        this.password = password;
        this.passwordCanBeModified = passwordCanBeModified;
        this.promotionCodes = promotionCodes;
        this.softPhoneAutoConnect = softPhoneAutoConnect;
        this.softPhoneEnable = softPhoneEnable;
        this.specialInfo = specialInfo;
        this.subscriptionId = subscriptionId;
        this.territoryCode = territoryCode;
        this.profileImageUrl = profileImageUrl;
    }

    @Generated(hash = 700875540)
    public ClientDetails() {
    }

    @Generated(hash = 1996440962)
    private transient String clientContactDetails__resolvedKey;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientContactDetailsId() {
        return clientContactDetailsId;
    }

    public void setClientContactDetailsId(String clientContactDetailsId) {
        this.clientContactDetailsId = clientContactDetailsId;
    }

    @JsonProperty("Active")
    public boolean isActive() {
        return active;
    }

    @JsonProperty("Active")
    public void setActive(boolean active) {
        this.active = active;
    }

    @JsonProperty("BillToId")
    public int getBillToId() {
        return billToId;
    }

    @JsonProperty("BillToId")
    public void setBillToId(int billToId) {
        this.billToId = billToId;
    }

    @Keep
    @JsonProperty("ClientContactDetails")
    public ClientContactDetails getClientContactDetails() {
        return clientContactDetails;
    }

    @Keep
    @JsonProperty("ClientContactDetails")
    public void setClientContactDetails(ClientContactDetails clientContactDetails) {
        this.clientContactDetails = clientContactDetails;
    }

    @JsonProperty("ClientId")
    public String getClientId() {
        return clientId;
    }

    @JsonProperty("ClientId")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @JsonProperty("CreditCardDetail")
    public String getCreditCardDetail() {
        return creditCardDetail;
    }

    @JsonProperty("CreditCardDetail")
    public void setCreditCardDetail(String creditCardDetail) {
        this.creditCardDetail = creditCardDetail;
    }

    @JsonProperty("DefaultLanguage")
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    @JsonProperty("DefaultLanguage")
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    @JsonProperty("Hold")
    public boolean isHold() {
        return hold;
    }

    @JsonProperty("Hold")
    public void setHold(boolean hold) {
        this.hold = hold;
    }

    @JsonProperty("OperationComments")
    public String getOperationComments() {
        return operationComments;
    }

    @JsonProperty("OperationComments")
    public void setOperationComments(String operationComments) {
        this.operationComments = operationComments;
    }

    @JsonProperty("Password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("Password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty("PasswordCanBeModified")
    public boolean getPasswordCanBeModified() {
        return passwordCanBeModified;
    }

    @JsonProperty("PasswordCanBeModified")
    public void setPasswordCanBeModified(boolean passwordCanBeModified) {
        this.passwordCanBeModified = passwordCanBeModified;
    }

    @JsonProperty("PromotionCodes")
    public List<String> getPromotionCodes() {
        return promotionCodes;
    }

    @JsonProperty("PromotionCodes")
    public void setPromotionCodes(List<String> promotionCodes) {
        this.promotionCodes = promotionCodes;
    }

    @JsonProperty("SoftPhoneAutoConnect")
    public boolean isSoftPhoneAutoConnect() {
        return softPhoneAutoConnect;
    }

    @JsonProperty("SoftPhoneAutoConnect")
    public void setSoftPhoneAutoConnect(boolean softPhoneAutoConnect) {
        this.softPhoneAutoConnect = softPhoneAutoConnect;
    }

    @JsonProperty("SoftPhoneEnable")
    public boolean isSoftPhoneEnable() {
        return softPhoneEnable;
    }

    @JsonProperty("SoftPhoneEnable")
    public void setSoftPhoneEnable(boolean softPhoneEnable) {
        this.softPhoneEnable = softPhoneEnable;
    }

    @JsonProperty("SpecialInfo")
    public String getSpecialInfo() {
        return specialInfo;
    }

    @JsonProperty("SpecialInfo")
    public void setSpecialInfo(String specialInfo) {
        this.specialInfo = specialInfo;
    }

    @JsonProperty("SubscriptionId")
    public String getSubscriptionId() {
        return subscriptionId;
    }

    @JsonProperty("SubscriptionId")
    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    @JsonProperty("TerritoryCode")
    public String getTerritoryCode() {
        return territoryCode;
    }

    @JsonProperty("TerritoryCode")
    public void setTerritoryCode(String territoryCode) {
        this.territoryCode = territoryCode;
    }

    public boolean getActive() {
        return this.active;
    }

    public boolean getHold() {
        return this.hold;
    }

    public boolean getSoftPhoneAutoConnect() {
        return this.softPhoneAutoConnect;
    }

    public boolean getSoftPhoneEnable() {
        return this.softPhoneEnable;
    }

    @JsonProperty("ProfileImageUrl")
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    @JsonProperty("ProfileImageUrl")
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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
    @Generated(hash = 2105535729)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getClientDetailsDao() : null;
    }

}
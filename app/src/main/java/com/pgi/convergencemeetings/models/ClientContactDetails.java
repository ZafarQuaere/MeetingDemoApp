package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Address",
    "Email",
    "Fax",
    "FaxIntlPrefix",
    "FirstName",
    "HomePhone",
    "HomePhoneExt",
    "HomePhoneIntlPrefix",
    "HomePhoneIsDefault",
    "JobTitle",
    "LastName",
    "MobilePhone",
    "MobilePhoneExt",
    "MobilePhoneIntlPrefix",
    "MobilePhoneIsDefault",
    "Phone",
    "PhoneExt",
    "PhoneIntlPrefix",
    "PhoneIsDefault",
    "SecondaryPhone",
    "SecondaryPhoneExt",
    "SecondaryPhoneIntlPrefix",
    "SecondaryPhoneIsDefault"
})
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class ClientContactDetails {

    @Id(autoincrement = true)
    @JsonProperty
    private Long id;
    @JsonProperty
    @Index
    private String addressId;

    @ToOne(joinProperty = "addressId")
    @JsonProperty("Address")
    private Address address;

    @JsonProperty("Email")
    private String email;
    @JsonProperty("Fax")
    private String fax;
    @JsonProperty("FaxIntlPrefix")
    private String faxIntlPrefix;
    @JsonProperty("FirstName")
    private String firstName;
    @JsonProperty("HomePhone")
    private String homePhone;
    @JsonProperty("HomePhoneExt")
    private String homePhoneExt;
    @JsonProperty("HomePhoneIntlPrefix")
    private String homePhoneIntlPrefix;
    @JsonProperty("HomePhoneIsDefault")
    private boolean homePhoneIsDefault;
    @JsonProperty("JobTitle")
    private String jobTitle;
    @JsonProperty("LastName")
    private String lastName;
    @JsonProperty("MobilePhone")
    private String mobilePhone;
    @JsonProperty("MobilePhoneExt")
    private String mobilePhoneExt;
    @JsonProperty("MobilePhoneIntlPrefix")
    private String mobilePhoneIntlPrefix;
    @JsonProperty("MobilePhoneIsDefault")
    private boolean mobilePhoneIsDefault;
    @JsonProperty("Phone")
    private String phone;
    @JsonProperty("PhoneExt")
    private String phoneExt;
    @JsonProperty("PhoneIntlPrefix")
    private String phoneIntlPrefix;
    @JsonProperty("PhoneIsDefault")
    private boolean phoneIsDefault;
    @JsonProperty("SecondaryPhone")
    private String secondaryPhone;
    @JsonProperty("SecondaryPhoneExt")
    private String secondaryPhoneExt;
    @JsonProperty("SecondaryPhoneIntlPrefix")
    private String secondaryPhoneIntlPrefix;
    @JsonProperty("SecondaryPhoneIsDefault")
    private boolean secondaryPhoneIsDefault;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1760623275)
    private transient ClientContactDetailsDao myDao;


    @Generated(hash = 1283065699)
    public ClientContactDetails(Long id, String addressId, String email, String fax,
                                String faxIntlPrefix, String firstName, String homePhone, String homePhoneExt,
                                String homePhoneIntlPrefix, boolean homePhoneIsDefault, String jobTitle,
                                String lastName, String mobilePhone, String mobilePhoneExt,
                                String mobilePhoneIntlPrefix, boolean mobilePhoneIsDefault, String phone,
                                String phoneExt, String phoneIntlPrefix, boolean phoneIsDefault,
                                String secondaryPhone, String secondaryPhoneExt, String secondaryPhoneIntlPrefix,
                                boolean secondaryPhoneIsDefault) {
        this.id = id;
        this.addressId = addressId;
        this.email = email;
        this.fax = fax;
        this.faxIntlPrefix = faxIntlPrefix;
        this.firstName = firstName;
        this.homePhone = homePhone;
        this.homePhoneExt = homePhoneExt;
        this.homePhoneIntlPrefix = homePhoneIntlPrefix;
        this.homePhoneIsDefault = homePhoneIsDefault;
        this.jobTitle = jobTitle;
        this.lastName = lastName;
        this.mobilePhone = mobilePhone;
        this.mobilePhoneExt = mobilePhoneExt;
        this.mobilePhoneIntlPrefix = mobilePhoneIntlPrefix;
        this.mobilePhoneIsDefault = mobilePhoneIsDefault;
        this.phone = phone;
        this.phoneExt = phoneExt;
        this.phoneIntlPrefix = phoneIntlPrefix;
        this.phoneIsDefault = phoneIsDefault;
        this.secondaryPhone = secondaryPhone;
        this.secondaryPhoneExt = secondaryPhoneExt;
        this.secondaryPhoneIntlPrefix = secondaryPhoneIntlPrefix;
        this.secondaryPhoneIsDefault = secondaryPhoneIsDefault;
    }

    @Generated(hash = 1033927791)
    public ClientContactDetails() {
    }

    @Generated(hash = 316314308)
    private transient String address__resolvedKey;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    @Keep
    @JsonProperty("Address")
    public Address getAddress() {
        return address;
    }

    @Keep
    @JsonProperty("Address")
    public void setAddress(Address address) {
        this.address = address;
    }

    @JsonProperty("Email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("Email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("Fax")
    public String getFax() {
        return fax;
    }

    @JsonProperty("Fax")
    public void setFax(String fax) {
        this.fax = fax;
    }

    @JsonProperty("FaxIntlPrefix")
    public String getFaxIntlPrefix() {
        return faxIntlPrefix;
    }

    @JsonProperty("FaxIntlPrefix")
    public void setFaxIntlPrefix(String faxIntlPrefix) {
        this.faxIntlPrefix = faxIntlPrefix;
    }

    @JsonProperty("FirstName")
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("FirstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonProperty("HomePhone")
    public String getHomePhone() {
        return homePhone;
    }

    @JsonProperty("HomePhone")
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    @JsonProperty("HomePhoneExt")
    public String getHomePhoneExt() {
        return homePhoneExt;
    }

    @JsonProperty("HomePhoneExt")
    public void setHomePhoneExt(String homePhoneExt) {
        this.homePhoneExt = homePhoneExt;
    }

    @JsonProperty("HomePhoneIntlPrefix")
    public String getHomePhoneIntlPrefix() {
        return homePhoneIntlPrefix;
    }

    @JsonProperty("HomePhoneIntlPrefix")
    public void setHomePhoneIntlPrefix(String homePhoneIntlPrefix) {
        this.homePhoneIntlPrefix = homePhoneIntlPrefix;
    }

    @JsonProperty("HomePhoneIsDefault")
    public boolean isHomePhoneIsDefault() {
        return homePhoneIsDefault;
    }

    @JsonProperty("HomePhoneIsDefault")
    public void setHomePhoneIsDefault(boolean homePhoneIsDefault) {
        this.homePhoneIsDefault = homePhoneIsDefault;
    }

    @JsonProperty("JobTitle")
    public String getJobTitle() {
        return jobTitle;
    }

    @JsonProperty("JobTitle")
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @JsonProperty("LastName")
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("LastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonProperty("MobilePhone")
    public String getMobilePhone() {
        return mobilePhone;
    }

    @JsonProperty("MobilePhone")
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @JsonProperty("MobilePhoneExt")
    public String getMobilePhoneExt() {
        return mobilePhoneExt;
    }

    @JsonProperty("MobilePhoneExt")
    public void setMobilePhoneExt(String mobilePhoneExt) {
        this.mobilePhoneExt = mobilePhoneExt;
    }

    @JsonProperty("MobilePhoneIntlPrefix")
    public String getMobilePhoneIntlPrefix() {
        return mobilePhoneIntlPrefix;
    }

    @JsonProperty("MobilePhoneIntlPrefix")
    public void setMobilePhoneIntlPrefix(String mobilePhoneIntlPrefix) {
        this.mobilePhoneIntlPrefix = mobilePhoneIntlPrefix;
    }

    @JsonProperty("MobilePhoneIsDefault")
    public boolean isMobilePhoneIsDefault() {
        return mobilePhoneIsDefault;
    }

    @JsonProperty("MobilePhoneIsDefault")
    public void setMobilePhoneIsDefault(boolean mobilePhoneIsDefault) {
        this.mobilePhoneIsDefault = mobilePhoneIsDefault;
    }

    @JsonProperty("Phone")
    public String getPhone() {
        return phone;
    }

    @JsonProperty("Phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonProperty("PhoneExt")
    public String getPhoneExt() {
        return phoneExt;
    }

    @JsonProperty("PhoneExt")
    public void setPhoneExt(String phoneExt) {
        this.phoneExt = phoneExt;
    }

    @JsonProperty("PhoneIntlPrefix")
    public String getPhoneIntlPrefix() {
        return phoneIntlPrefix;
    }

    @JsonProperty("PhoneIntlPrefix")
    public void setPhoneIntlPrefix(String phoneIntlPrefix) {
        this.phoneIntlPrefix = phoneIntlPrefix;
    }

    @JsonProperty("PhoneIsDefault")
    public boolean isPhoneIsDefault() {
        return phoneIsDefault;
    }

    @JsonProperty("PhoneIsDefault")
    public void setPhoneIsDefault(boolean phoneIsDefault) {
        this.phoneIsDefault = phoneIsDefault;
    }

    @JsonProperty("SecondaryPhone")
    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    @JsonProperty("SecondaryPhone")
    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    @JsonProperty("SecondaryPhoneExt")
    public String getSecondaryPhoneExt() {
        return secondaryPhoneExt;
    }

    @JsonProperty("SecondaryPhoneExt")
    public void setSecondaryPhoneExt(String secondaryPhoneExt) {
        this.secondaryPhoneExt = secondaryPhoneExt;
    }

    @JsonProperty("SecondaryPhoneIntlPrefix")
    public String getSecondaryPhoneIntlPrefix() {
        return secondaryPhoneIntlPrefix;
    }

    @JsonProperty("SecondaryPhoneIntlPrefix")
    public void setSecondaryPhoneIntlPrefix(String secondaryPhoneIntlPrefix) {
        this.secondaryPhoneIntlPrefix = secondaryPhoneIntlPrefix;
    }

    @JsonProperty("SecondaryPhoneIsDefault")
    public boolean isSecondaryPhoneIsDefault() {
        return secondaryPhoneIsDefault;
    }

    @JsonProperty("SecondaryPhoneIsDefault")
    public void setSecondaryPhoneIsDefault(boolean secondaryPhoneIsDefault) {
        this.secondaryPhoneIsDefault = secondaryPhoneIsDefault;
    }

    public boolean getHomePhoneIsDefault() {
        return this.homePhoneIsDefault;
    }

    public boolean getMobilePhoneIsDefault() {
        return this.mobilePhoneIsDefault;
    }

    public boolean getPhoneIsDefault() {
        return this.phoneIsDefault;
    }

    public boolean getSecondaryPhoneIsDefault() {
        return this.secondaryPhoneIsDefault;
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
    @Generated(hash = 1515358534)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getClientContactDetailsDao() : null;
    }

}
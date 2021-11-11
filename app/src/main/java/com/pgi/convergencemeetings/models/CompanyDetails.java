package com.pgi.convergencemeetings.models;

/**
 * Created by nnennaiheke on 8/9/17.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "BlockedServices",
    "CompanyId",
    "Name"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class CompanyDetails {

    @Id(autoincrement = true)
    @JsonProperty
    private Long id;

    @Convert(converter = GreenConverter.class, columnType = String.class)
    @JsonProperty("BlockedServices")
    private List<String> blockedServices = null;

    @JsonProperty("CompanyId")
    private int companyId;
    @JsonProperty("Name")
    private String name;

    @Generated(hash = 819248028)
    public CompanyDetails(Long id, List<String> blockedServices, int companyId,
                          String name) {
        this.id = id;
        this.blockedServices = blockedServices;
        this.companyId = companyId;
        this.name = name;
    }

    @Generated(hash = 1725163804)
    public CompanyDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("BlockedServices")
    public List<String> getBlockedServices() {
        return blockedServices;
    }

    @JsonProperty("BlockedServices")
    public void setBlockedServices(List<String> blockedServices) {
        this.blockedServices = blockedServices;
    }

    @JsonProperty("CompanyId")
    public int getCompanyId() {
        return companyId;
    }

    @JsonProperty("CompanyId")
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Name")
    public void setName(String name) {
        this.name = name;
    }

}
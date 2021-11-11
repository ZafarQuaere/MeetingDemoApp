
package com.pgi.convergencemeetings.models.settings;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "CompanyId",
    "CompanyName",
    "CorporateCustomerId",
    "CorporateCustomerName",
    "EnterpriseId",
    "EnterpriseName",
    "EnterpriseType",
    "HubGroupId",
    "HubGroupName",
    "HubId",
    "HubName",
    "HubUrl",
    "ProviderId",
    "ProviderName",
    "ProviderType"
})
public class ClientHierarchy {

    @JsonProperty("CompanyId")
    private Integer companyId;
    @JsonProperty("CompanyName")
    private String companyName;
    @JsonProperty("CorporateCustomerId")
    private Integer corporateCustomerId;
    @JsonProperty("CorporateCustomerName")
    private String corporateCustomerName;
    @JsonProperty("EnterpriseId")
    private Integer enterpriseId;
    @JsonProperty("EnterpriseName")
    private String enterpriseName;
    @JsonProperty("EnterpriseType")
    private String enterpriseType;
    @JsonProperty("HubGroupId")
    private Integer hubGroupId;
    @JsonProperty("HubGroupName")
    private String hubGroupName;
    @JsonProperty("HubId")
    private Integer hubId;
    @JsonProperty("HubName")
    private String hubName;
    @JsonProperty("HubUrl")
    private String hubUrl;
    @JsonProperty("ProviderId")
    private Integer providerId;
    @JsonProperty("ProviderName")
    private String providerName;
    @JsonProperty("ProviderType")
    private String providerType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("CompanyId")
    public Integer getCompanyId() {
        return companyId;
    }

    @JsonProperty("CompanyId")
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    @JsonProperty("CompanyName")
    public String getCompanyName() {
        return companyName;
    }

    @JsonProperty("CompanyName")
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @JsonProperty("CorporateCustomerId")
    public Integer getCorporateCustomerId() {
        return corporateCustomerId;
    }

    @JsonProperty("CorporateCustomerId")
    public void setCorporateCustomerId(Integer corporateCustomerId) {
        this.corporateCustomerId = corporateCustomerId;
    }

    @JsonProperty("CorporateCustomerName")
    public String getCorporateCustomerName() {
        return corporateCustomerName;
    }

    @JsonProperty("CorporateCustomerName")
    public void setCorporateCustomerName(String corporateCustomerName) {
        this.corporateCustomerName = corporateCustomerName;
    }

    @JsonProperty("EnterpriseId")
    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    @JsonProperty("EnterpriseId")
    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    @JsonProperty("EnterpriseName")
    public String getEnterpriseName() {
        return enterpriseName;
    }

    @JsonProperty("EnterpriseName")
    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    @JsonProperty("EnterpriseType")
    public String getEnterpriseType() {
        return enterpriseType;
    }

    @JsonProperty("EnterpriseType")
    public void setEnterpriseType(String enterpriseType) {
        this.enterpriseType = enterpriseType;
    }

    @JsonProperty("HubGroupId")
    public Integer getHubGroupId() {
        return hubGroupId;
    }

    @JsonProperty("HubGroupId")
    public void setHubGroupId(Integer hubGroupId) {
        this.hubGroupId = hubGroupId;
    }

    @JsonProperty("HubGroupName")
    public String getHubGroupName() {
        return hubGroupName;
    }

    @JsonProperty("HubGroupName")
    public void setHubGroupName(String hubGroupName) {
        this.hubGroupName = hubGroupName;
    }

    @JsonProperty("HubId")
    public Integer getHubId() {
        return hubId;
    }

    @JsonProperty("HubId")
    public void setHubId(Integer hubId) {
        this.hubId = hubId;
    }

    @JsonProperty("HubName")
    public String getHubName() {
        return hubName;
    }

    @JsonProperty("HubName")
    public void setHubName(String hubName) {
        this.hubName = hubName;
    }

    @JsonProperty("HubUrl")
    public String getHubUrl() {
        return hubUrl;
    }

    @JsonProperty("HubUrl")
    public void setHubUrl(String hubUrl) {
        this.hubUrl = hubUrl;
    }

    @JsonProperty("ProviderId")
    public Integer getProviderId() {
        return providerId;
    }

    @JsonProperty("ProviderId")
    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    @JsonProperty("ProviderName")
    public String getProviderName() {
        return providerName;
    }

    @JsonProperty("ProviderName")
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    @JsonProperty("ProviderType")
    public String getProviderType() {
        return providerType;
    }

    @JsonProperty("ProviderType")
    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}

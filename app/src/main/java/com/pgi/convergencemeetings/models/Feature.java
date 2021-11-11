package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by nnennaiheke on 8/4/17.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Description",
    "FeatureCode",
    "Name"
})

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Feature {
    @JsonIgnore

    @Id(autoincrement = true)
    private Long id;

    private String featuresId;

    @JsonProperty("Description")
    private String description;
    @JsonProperty("FeatureCode")
    private String featureCode;
    @JsonProperty("Name")
    private String name;

    @Generated(hash = 67922871)
    public Feature(Long id, String featuresId, String description,
                   String featureCode, String name) {
        this.id = id;
        this.featuresId = featuresId;
        this.description = description;
        this.featureCode = featureCode;
        this.name = name;
    }

    @Generated(hash = 829817725)
    public Feature() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeaturesId() {
        return featuresId;
    }

    public void setFeaturesId(String featuresId) {
        this.featuresId = featuresId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

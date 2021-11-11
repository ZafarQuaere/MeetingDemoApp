package com.pgi.convergencemeetings.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by nnennaiheke on 8/4/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class Errors {

    @Id(autoincrement = true)
    public Long id;
    public String errorsId;

    @Generated(hash = 433517740)
    public Errors(Long id, String errorsId) {
        this.id = id;
        this.errorsId = errorsId;
    }

    @Generated(hash = 1752878376)
    public Errors() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getErrorsId() {
        return errorsId;
    }

    public void setErrorsId(String errorsId) {
        this.errorsId = errorsId;
    }
}
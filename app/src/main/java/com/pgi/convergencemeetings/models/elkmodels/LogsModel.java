package com.pgi.convergencemeetings.models.elkmodels;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by ashwanikumar on 22-02-2018.
 */
@Entity
public class LogsModel {
    @Id(autoincrement = true)
    @JsonProperty
    private Long id;

    @JsonProperty("logs")
    private String logs;
    @JsonProperty("isuploaded")
    private String isuploaded;

    @Generated(hash = 1250125826)
    public LogsModel(Long id, String logs, String isuploaded) {
        this.id = id;
        this.logs = logs;
        this.isuploaded = isuploaded;
    }

    @Generated(hash = 303994793)
    public LogsModel() {
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public String getIsuploaded() {
        return isuploaded;
    }

    public void setIsuploaded(String isuploaded) {
        this.isuploaded = isuploaded;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
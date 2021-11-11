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
public class ReservationOptions {

    @Id(autoincrement = true)
    private Long id;
    private String reservationOptionsId;

    @Generated(hash = 975135811)
    public ReservationOptions(Long id, String reservationOptionsId) {
        this.id = id;
        this.reservationOptionsId = reservationOptionsId;
    }

    @Generated(hash = 1655734501)
    public ReservationOptions() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationOptionsId() {
        return reservationOptionsId;
    }

    public void setReservationOptionsId(String reservationOptionsId) {
        this.reservationOptionsId = reservationOptionsId;
    }
}
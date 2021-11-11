package com.pgi.convergencemeetings.models;

import com.pgi.convergence.enums.HostControlsEnum;

/**
 * Created by surbhidhingra on 07-02-18.
 */

public class HostControlsModel {
    private HostControlsEnum hostControlType;
    private String controlName;

    public HostControlsEnum getHostControlType() {
        return hostControlType;
    }

    public void setHostControlType(HostControlsEnum hostControlType) {
        this.hostControlType = hostControlType;
    }

    public String getControlName() {
        return controlName;
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }
}

package com.pgi.convergence.enums.pia;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by surbhidhingra on 25-10-17.
 */

public enum PIAWebSocketCommand {
    @JsonProperty("bind")
    BIND,
    @JsonProperty("unbind")
    UNBIND,
    @JsonProperty("keepalive")
    KEEPALIVE
}

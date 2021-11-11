package com.pgi.convergence.enums;

/**
 * Created by visheshchandra on 3/14/2018.
 */

public enum ChatMessageState {
    RECEIVED("Received"), SENDING("Sending..."), FAILED("Not delivered");

    private String value;

    ChatMessageState(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}

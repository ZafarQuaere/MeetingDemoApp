package com.pgi.network;

/**
 * The interface Pia web socket response handler.
 */
public interface PIAWebSocketResponseHandler {

    /**
     * On event message received.
     *
     * @param event the event
     * @param data  the data
     */
    void onEventMessageReceived(String event, String data);

    /**
     * On command message received.
     *
     * @param data the data
     */
    void onCommandMessageReceived(String data);
}

package com.pgi.network;

/**
 * This class craete and provide the instace of {@link PIAWebSocket} to consume its web service.
 */
public class PIAWebSocketManager {

    private static PIAWebSocket sPiaWebSocket;
    private PIAWebSocketResponseHandler mPiaWebSocketResponseHandler;

    /**
     * Instantiates a new Pia web socket manager.
     *
     * @param piaWebSocketResponseHandler the pia web socket response handler
     */
    public PIAWebSocketManager(PIAWebSocketResponseHandler piaWebSocketResponseHandler) {
        mPiaWebSocketResponseHandler = piaWebSocketResponseHandler;
    }

    /**
     * Invoke pia socket.
     *
     * @param bindPayload the bind payload
     */
    public void invokePIASocket(String bindPayload) {
        sPiaWebSocket = PIAWebSocket.Builder.with(BuildConfig.PIA_WEB_SOCKET_URL).build(mPiaWebSocketResponseHandler);
        sPiaWebSocket.connect();
        sendBindCommand(bindPayload);
    }

    /**
     * Send bind command.
     *
     * @param bindPayload the bind payload
     */
    public void sendBindCommand(String bindPayload) {
        if (sPiaWebSocket != null) {
            sPiaWebSocket.send(bindPayload);
        }
    }

    /**
     * Send un bind command.
     *
     * @param unbindPayload the unbind payload
     */
    public void sendUnBindCommand(String unbindPayload) {
        if (sPiaWebSocket != null) {
            sPiaWebSocket.send(unbindPayload);
            sPiaWebSocket.clearListeners();
            sPiaWebSocket.close();
            sPiaWebSocket.terminate();
            sPiaWebSocket = null;
        }
    }

    /**
     * Send keep alive command for socket.
     *
     * @param keepAlivePayload the keep alive payload
     */
    public void sendKeepAliveCommand(String keepAlivePayload) {
        if (sPiaWebSocket != null) {
            sPiaWebSocket.send(keepAlivePayload);
        }
    }
}

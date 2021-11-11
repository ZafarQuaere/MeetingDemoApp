package com.pgi.network;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.ws.RealWebSocket;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.ByteString;

/**
 * This class provides the web socket and maintain the states of socket.
 * Provides call backs when socket connection state change.
 */

class PIAWebSocket {

    private final static String TAG = PIAWebSocket.class.getSimpleName();
    private final static String CLOSE_REASON = "End of session";
    private final static String WS_MSG_TYPE = "MsgType";
    private final static String WS_COMMAND = "command";
    private final static int MAX_COLLISION = 7;

    /**
     * Websocket sState
     */
    private static State sState;
    /**
     * Websocket main sRequest
     */
    private static Request sRequest;
    /**
     * Websocket connection
     */
    private static RealWebSocket sMeetingWebSocket;
    /**
     * Reconnection post delayed handler
     */
    private static Handler sDelayedReconnection;
    /**
     * Websocket events new message listeners
     */
    private static PIAWebSocketResponseHandler sPiaWebSocketResponseHandler;
    /**
     * Message list to be send onEvent open {@link State#OPEN} connection sState
     */
    private static List<String> sOpenMessageQueue = new ArrayList<>();
    /**
     * Websocket sState change listener
     */
    private static OnStateChangeListener sOnChangeStateListener;
    /**
     * Number of reconnection attempts
     */
    private static int sReconnectionAttempts;
    private static boolean sSkipOnFailure;

    /**
     * Main socket states
     */
    public enum State {
        CLOSED, CLOSING, CONNECT_ERROR, RECONNECT_ATTEMPT, RECONNECTING, OPENING, OPEN
    }

    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.HEADERS : HttpLoggingInterceptor.Level.NONE);

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder()
                    .callTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(logging);

    public static class Builder {

        private Request.Builder request;

        private Builder(Request.Builder request) {
            this.request = request;
        }

        public static Builder with(@NonNull String url) {
            // Silently replace web socket URLs with HTTP URLs.
            if (!url.regionMatches(true, 0, "ws:", 0, 3) && !url.regionMatches(true, 0, "wss:", 0, 4))
                throw new IllegalArgumentException("web socket url must start with ws or wss, passed url is " + url);

            return new Builder(new Request.Builder().url(url));
        }

        public PIAWebSocket build(PIAWebSocketResponseHandler piaWebSocketResponseHandler) {
            return new PIAWebSocket(request.build(), piaWebSocketResponseHandler);
        }
    }

    private PIAWebSocket(Request request, PIAWebSocketResponseHandler piaWebSocketResponseHandler) {
        PIAWebSocket.sRequest = request;
        sState = State.CLOSED;
        sPiaWebSocketResponseHandler = piaWebSocketResponseHandler;
        sDelayedReconnection = new Handler(Looper.getMainLooper());
        sSkipOnFailure = false;
    }

    /**
     * Start socket connection if it's not already started
     */
    public PIAWebSocket connect() {
        if (httpClient == null) {
            throw new IllegalStateException("Make sure to use Socket.Builder before using Socket#connect.");
        }
        if (sMeetingWebSocket == null) {
            try {
                sMeetingWebSocket = (RealWebSocket) httpClient.build().newWebSocket(sRequest, webSocketListener);
                changeState(State.OPENING);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        } else if (sState == State.CLOSED) {
            sMeetingWebSocket.connect(httpClient.build());
            changeState(State.OPENING);
        }
        return this;
    }

    /**
     * Set listener which fired every time message received with contained data.
     *
     * @param listener message on arrive listener
     */
    public PIAWebSocket onEvent(@NonNull String event, @NonNull OnEventListener listener) {
        return this;
    }

    /**
     * Set listener which fired every time message received with contained data.
     *
     * @param listener message on arrive listener
     */
    public PIAWebSocket onEventResponse(@NonNull String event, @NonNull PIAWebSocketResponseHandler listener) {
        return this;
    }

    /**
     * Send message in {event->data} format
     *
     * @param data message data in JSON format
     * @return true if the message send/on socket send quest; false otherwise
     */
    public boolean send(@NonNull String data) {
        if (sMeetingWebSocket != null) {
            Log.v(TAG, "Try to send data " + data);
            return sMeetingWebSocket.send(data);
        }
        return false;
    }

    /**
     * Set sState listener which fired every time {@link PIAWebSocket#sState} changed.
     *
     * @param listener sState change listener
     */
    public PIAWebSocket setOnChangeStateListener(@NonNull OnStateChangeListener listener) {
        sOnChangeStateListener = listener;
        return this;
    }

    /**
     * Clear all socket listeners in one line
     */
    public void clearListeners() {
        sOnChangeStateListener = null;
    }

    /**
     * Send normal close sRequest to the host
     */
    public void close() {
        if (sMeetingWebSocket != null) {
            sMeetingWebSocket.close(1000, CLOSE_REASON);
        }
    }

    /**
     * Send close sRequest to the host
     */
    public void close(int code, @NonNull String reason) {
        if (sMeetingWebSocket != null) {
            sMeetingWebSocket.close(code, reason);
        }
    }

    /**
     * Terminate the socket connection permanently
     */
    public void terminate() {
        sSkipOnFailure = true;
        if (sMeetingWebSocket != null) {
            sMeetingWebSocket.cancel();
            sMeetingWebSocket = null;
        }
    }

    /**
     * Add message in a queue if the socket not open and send them
     * if the socket opened
     *
     * @param data message data in JSON format
     */
    public void sendOnOpen(@NonNull String data) {
        if (sState != State.OPEN)
            sOpenMessageQueue.add(data);
        else
            send(data);
    }

    /**
     * Retrieve current socket connection sState {@link State}
     */
    public State getState() {
        return sState;
    }

    /**
     * Change current sState and call listener method with new sState
     * {@link OnStateChangeListener#onChange(PIAWebSocket, State)}
     *
     * @param newState new sState
     */
    private void changeState(State newState) {
        sState = newState;
        if (sOnChangeStateListener != null) {
            sOnChangeStateListener.onChange(PIAWebSocket.this, sState);
        }
    }

    /**
     * Try to reconnect to the websocket after delay time using <i>Exponential backoff</i> method.
     */
    private void reconnect() {
        if (sState != State.CONNECT_ERROR)
            return;

        changeState(State.RECONNECT_ATTEMPT);

        if (sMeetingWebSocket != null) {
            sMeetingWebSocket.cancel();
            sMeetingWebSocket = null;
        }

        int collision = sReconnectionAttempts > MAX_COLLISION ? MAX_COLLISION : sReconnectionAttempts;
        long delayTime = Math.round((Math.pow(2, collision) - 1) / 2) * 1000;

        sDelayedReconnection.removeCallbacksAndMessages(null);
        sDelayedReconnection.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeState(State.RECONNECTING);
                sReconnectionAttempts++;
                connect();
            }
        }, delayTime);
    }

    private WebSocketListener webSocketListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.v(TAG, "Socket has been opened successfully.");
            sReconnectionAttempts = 0;

            for (String event : sOpenMessageQueue) {
                send(event);
            }
            sOpenMessageQueue.clear();

            changeState(State.OPEN);
        }

        @Override
        public void onMessage(WebSocket webSocket, final String text) {
            Log.v(TAG, "New Message received " + text);

            try {
                JSONObject response = new JSONObject(text);
                if (text.contains(WS_MSG_TYPE)) {
                    final String event = response.getString(WS_MSG_TYPE);

                    if (event != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                sPiaWebSocketResponseHandler.onEventMessageReceived(event, text);
                            }
                        });
                    }
                } else if (text.contains(WS_COMMAND)) {
                    String command = response.getString(WS_COMMAND);
                    if (command != null) {
                        sPiaWebSocketResponseHandler.onCommandMessageReceived(text);
                    }
                }

            } catch (JSONException e) {
                Log.e(TAG, "Unknown message format.");
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            Log.v(TAG, "Close sRequest from server with reason '" + reason + "'");
            changeState(State.CLOSING);
            webSocket.close(1000, reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.v(TAG, "Socket connection closed with reason '" + reason + "'");
            changeState(State.CLOSED);
        }

        /**
         * This method call if:
         * - Fail to verify websocket GET sRequest  => Throwable {@link ProtocolException}
         * - Can't establish websocket connection after upgrade GET sRequest => response null, Throwable {@link Exception}
         * - First GET sRequest had been failed => response null, Throwable {@link java.io.IOException}
         * - Fail to send Ping => response null, Throwable {@link java.io.IOException}
         * - Fail to send data frame => response null, Throwable {@link java.io.IOException}
         * - Fail to read data frame => response null, Throwable {@link java.io.IOException}
         */
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            if (!sSkipOnFailure) {
                sSkipOnFailure = false;
                Log.v(TAG, "Socket connection fail, try to reconnect. (" + sReconnectionAttempts + ")");
                changeState(State.CONNECT_ERROR);
                reconnect();
            }
        }
    };

    public abstract static class OnMessageListener {
        public abstract void onMessage(String data);

        /**
         * Method called from socket to execute listener implemented in
         * {@link #onMessage(String)} on main thread
         *
         * @param socket Socket that receive the message
         * @param data   Data string received
         */
        private void onMessage(PIAWebSocket socket, final String data) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onMessage(data);
                }
            });
        }
    }

    public abstract static class OnEventListener {
        public abstract void onMessage(String event);

        private void onMessage(PIAWebSocket socket, final String event) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onMessage(event);
                }
            });
        }
    }

    public abstract static class OnStateChangeListener {
        /**
         * Method need to override in listener usage
         */
        public abstract void onChange(State status);

        /**
         * Method called from socket to execute listener implemented in
         * {@link #onChange(State)} on main thread
         *
         * @param socket Socket that receive the message
         * @param status new status
         */
        private void onChange(PIAWebSocket socket, final State status) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onChange(status);
                }
            });
        }
    }
}

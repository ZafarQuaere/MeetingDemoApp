package com.pgi.convergencemeetings.base.services.retry_callbacks;

import com.google.common.base.MoreObjects;

/**
 * Created by nnennaiheke on 3/14/18.
 */


/**
 * This class handles the retry failure we may experience in our app. There are some scenarios where we do not
 * give the user any information, and others where we show either a toast message regarding the faiure or an alert
 * prompting the user to choose re-joining the meeting or canceling the session altoghther
 */

public class RetryStatus {

        public static final int JOIN_MEETING                       =  0;
        public static final int START_MEETING                      =  1;
        public static final int WS_CLIENT_INFO_SELF                =  2;
        public static final int WS_CLIENT_INFO_OTHERS              =  3;
        public static final int UAPI_SESSION                       =  4;
        public static final int UAPI_PARTICIPANT_JOIN_MEETING      =  5;
        public static final int UAPI_ROOM_INFO                     =  6;
        public static final int WS_LAST_USED_PHONE                 =  7;
        public static final int WS_GET_PHONE_NUMBERS               =  8;
        public static final int UAPI_DIALOUT                       = 9;
        public static final int WS_MEETING_ROOM_SEARCH             = 10;
        public static final int WS_ENTERPRISE_SEARCH               = 11;
        public static final int UAPI_MEETING_EVENTS                = 12;
        public static final int UAPI_END_MEETING                   = 13;
        public static final int UAPI_LEAVE_MEETING                 = 14;
        public static final int UAPI_MUTE_PARTICIPANT              = 15;
        public static final int UAPI_DISMISS_AUDIO_PARTICIPANT     = 16;
        public static final int UAPI_DEMOTE_PARTICIPANT            = 17;
        public static final int UAPI_PROMOTE_PARTICIPANT           = 18;
        public static final int PIA_SUBSCRIBE                      = 19;
        public static final int PIA_GET_CONFERENCE_STATE           = 20;
        public static final int PIA_UNSUSCRIBE                     = 21;
        public static final int PIA_SET_CONFERENCE_WATCH           = 22;
        public static final int PIA_START_CONFERENCE               = 23;
        public static final int PIA_SET_CONFERENCE_OPTION          = 24;
        public static final int PIA_SET_PARTICIPANT_OPTION         = 25;
        public static final int PIA_HANGUP                         = 26;
        public static final int PIA_END_CONFERENCE                 = 27;
        public static final int PIA_CLEAR_CONFERENCE_WATCH         = 28;
        public static final int PIA_DIALOUT                        = 29;
        public static final int PIA_MEETING_EVENTS                 = 30;
        public static final int PIA_SET_PARTICIPANT_INFO           = 31;
        public static final int FAILED                             = 32;
        public static final int NOCONNECTIVITY                     = 33;
        public static final int FLAKYCONNECTIVITY                  = 34;
        public static final int UAPI_WAITING_PARTICIPANT_ADMITTED  = 35;
        public static final int UAPI_WAITING_PARTICIPANT_DENIED    = 36;



        public int value() {
            return value;
        }

        public int reason() {
            return reason;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("value", value)
                    .add("reason", reason)
                    .toString();
        }

        public RetryStatus(int reason, int value) {
            this.value  = value;
            this.reason = reason;
        }

        private final int value;
        private final int reason;
    }


package com.pgi.convergencemeetings.utils;

import com.pgi.convergencemeetings.models.ConferenceState;
import com.pgi.convergencemeetings.models.MeetingParticipant;

/**
 * Created by ashwanikumar on 10/17/2017.
 */
public class ConferenceStateCache {

    private ConferenceState mConferenceState;
    private static ConferenceStateCache mConferenceInfo;
    private MeetingParticipant myPartInfo;

    private ConferenceStateCache(){

    }

    public static ConferenceStateCache getInstance(){
        if(mConferenceInfo == null){
            mConferenceInfo = new ConferenceStateCache();
        }
        return mConferenceInfo;
    }


    public void setConferenceState(ConferenceState piaWSMessage) {
        mConferenceState = piaWSMessage;
    }

    public ConferenceState getConferenceState() {
        return mConferenceState;
    }

    public void clearConferenceState(){
        mConferenceState = null;
    }

    public MeetingParticipant[] getParticipants(){
        MeetingParticipant[] participantsList = null;
        if(mConferenceState != null) {
            participantsList = mConferenceState.getMeetingParticipants();
        }
        return participantsList;
    }

    public void setMyPartInfo(MeetingParticipant myPartInfo) {
        this.myPartInfo = myPartInfo;
    }

    public MeetingParticipant getMyPartInfo(){
        return this.myPartInfo;
    }
}

package com.pgi.convergencemeetings.utils;

import com.pgi.network.models.ImeetingRoomInfo;

/**
 * Created by amit1829 on 10/26/2017.
 * This interface must be implemented by activities that contain this
 * DialInFragmentContract to allow an interaction in this DialInFragmentContract to be communicated
 * to the DialInActivityContract and potentially other fragments contained in that
 * DialInActivityContract.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */

public interface JoinMeetingsListener {
    void onRecentMeetingClick(ImeetingRoomInfo item);
}

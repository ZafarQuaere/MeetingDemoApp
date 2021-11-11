package com.pgi.convergencemeetings;

import com.pgi.convergencemeetings.search.ui.JoinMeetingFragment;
import com.pgi.convergencemeetings.search.ui.SearchActivity;

import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

/**
 * Created by ashwanikumar on 8/31/2017.
 */
@Implements(JoinMeetingFragment.class)
public class RecentMeetingsFragmentShadow {
    public RecentMeetingsFragmentShadow(){

    }

    @Implementation
    public SearchActivity getActivity(){
        ActivityController<SearchActivity> controller = Robolectric.buildActivity(SearchActivity.class);
        SearchActivity recentMeetingsActivity = controller.get();
        return recentMeetingsActivity;
    }
}

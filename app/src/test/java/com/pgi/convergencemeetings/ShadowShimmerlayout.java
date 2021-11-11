package com.pgi.convergencemeetings;

import android.content.Context;
import android.view.View;

import com.pgi.convergencemeetings.meeting.gm5.ui.audio.ShimmerLayout;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowViewGroup;

/**
 * Created by ashwanikumar on 16-02-2018.
 */
@Implements(ShimmerLayout.class)
public class ShadowShimmerlayout extends ShadowViewGroup {

    @Implementation
    public void prepareAnimation(Context context, View targetView){

    }

    @Implementation
    public void startShimmerAnimation() {

    }

    @Implementation
    public void stopShimmerAnimation() {

    }

}

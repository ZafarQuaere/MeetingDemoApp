package com.pgi.convergencemeetings;

import android.app.Application;

/**
 * Created by nnennaiheke on 8/11/17.
 */

public class SimpleApplication extends Application {
    /* For some reason Robolectric will not let you override the application in your manifest
        with just android.app.Application. So a subclass is being provided here to achieve this.
     */
}

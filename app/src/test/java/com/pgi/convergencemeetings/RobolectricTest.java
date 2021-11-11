package com.pgi.convergencemeetings;

import com.pgi.convergencemeetings.application.TestApplication;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by nnennaiheke on 8/11/17.
 */

@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "javax.net.ssl.*",
        "javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*",
        "com.android.internal.*", "net.openid.appauth.*", "androidx.*",
        "com.pgi.convergencemeetings.widget.*", "com.pgi.convergence.utils.CustomViewPager"})
@Config(application = TestApplication.class, sdk = 28)
public abstract class RobolectricTest {
}

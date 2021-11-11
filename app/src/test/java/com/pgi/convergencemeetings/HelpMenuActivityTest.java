package com.pgi.convergencemeetings;

import com.pgi.convergencemeetings.leftnav.help.ui.HelpMenuActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.rule.PowerMockRule;

public class HelpMenuActivityTest extends RobolectricTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();
    @Mock
    HelpMenuActivity helpMenuActivity;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testOnCall(){
        helpMenuActivity.onCall();
    }
    @Test
    public void testOnEmail(){
        helpMenuActivity.onEmail();
    }
    @Test
    public void testOnVisit(){
        helpMenuActivity.onVisit();
    }
}

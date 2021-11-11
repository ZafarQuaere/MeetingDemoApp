package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.WebConferenceOption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by ashwanikumar on 8/22/2017.
 */

public class WebConferenceOptionTest extends RobolectricTest {

    WebConferenceOption webConferenceOption;

    @Before
    public void setUp() throws Exception {
        webConferenceOption = new WebConferenceOption(0L, null, null, false, false);
    }

    @Test
    public void testGetId(){
        Random random = new Random();
        long expected = random.nextLong();
        webConferenceOption.setId(expected);
        long actual = webConferenceOption.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParameterizedConstructor(){
        Random random = new Random();
        long expectedId = random.nextLong();
        String expectedCode = "2412312";
        String expectedConferenceId = "52341433542343421";
        boolean expectedEnabled = true;
        boolean expectedLocked = false;
        webConferenceOption = new WebConferenceOption(expectedId, expectedConferenceId,expectedCode, expectedEnabled, expectedLocked);
        Assert.assertEquals(expectedId, (long) webConferenceOption.getId());
        Assert.assertEquals(expectedConferenceId, webConferenceOption.getWebConferenceOptionsId());
        Assert.assertEquals(expectedCode, webConferenceOption.getCode());
        Assert.assertEquals(expectedEnabled, webConferenceOption.getEnabled());
        Assert.assertEquals(expectedLocked, webConferenceOption.getLocked());
    }

    @Test
    public void testGetID(){
        Random random = new Random();
        long expectedId = random.nextLong();
        webConferenceOption.setId(expectedId);
        Assert.assertEquals(expectedId, (long) webConferenceOption.getId());
    }

    @Test
    public void testGetCode(){
        String expectedCode = "1234141";
        webConferenceOption.setCode(expectedCode);
        Assert.assertEquals(expectedCode, webConferenceOption.getCode());
    }

    @Test
    public void testGetEnabled(){
        webConferenceOption.setEnabled(true);
        Assert.assertTrue(webConferenceOption.getEnabled());
    }

    @Test
    public void testGetLocked(){
        webConferenceOption.setLocked(true);
        Assert.assertTrue(webConferenceOption.getLocked());
    }
}

package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.ConferenceOption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class ConferenceOptionTest extends RobolectricTest {

    private ConferenceOption conferenceOption;

    @Before
    public void setUp() throws Exception {
        conferenceOption = new ConferenceOption(0L, null, true, null);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        conferenceOption.setId(expected);
        long actual = conferenceOption.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetConferenceOptionId() {
        String expected = "ConferenceOption";
        conferenceOption.setConferenceOptionsId(expected);
        String actual = conferenceOption.getConferenceOptionsId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetEnabled() {
        conferenceOption.setEnabled(true);
        boolean actual = conferenceOption.getEnabled();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetName() {
        String expected = "Name";
        conferenceOption.setName(expected);
        String actual = conferenceOption.getName();
        Assert.assertEquals(expected, actual);
    }
}

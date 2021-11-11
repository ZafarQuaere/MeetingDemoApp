package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.Attendee;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by amit1829 on 8/4/17.
 */

public class AttendeeTest extends RobolectricTest {

    private Attendee attendee;

    @Before
    public void setUp() throws Exception {
        attendee = new Attendee(0L, null, true, true, null, true, true, false);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        attendee.setId(expected);
        long actual = attendee.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetAttendeesId() {
        String expected = "attendeesId";
        attendee.setAttendeesId(expected);
        String actual = attendee.getAttendeesId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetApplied() {
        attendee.setApplied(true);
        boolean actual = attendee.getApplied();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetAttended() {
        attendee.setAttended(true);
        boolean actual = attendee.getAttended();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetEmail() {
        String expected = "amit.kumar@pgi.com";
        attendee.setEmail(expected);
        String actual = attendee.getEmail();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetInvited() {
        attendee.setInvited(true);
        boolean actual = attendee.getInvited();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetModerator() {
        attendee.setModerator(true);
        boolean actual = attendee.getModerator();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetRegistered() {
        attendee.setRegistered(false);
        boolean actual = attendee.getRegistered();
        Assert.assertFalse(actual);
    }
}

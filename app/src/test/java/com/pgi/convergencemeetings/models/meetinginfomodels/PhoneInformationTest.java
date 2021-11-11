package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.PhoneInformation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class PhoneInformationTest extends RobolectricTest {

    private PhoneInformation phoneInformation;

    @Before
    public void setUp() throws Exception {
        phoneInformation = new PhoneInformation(0L, null, null, null, null, null);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        phoneInformation.setId(expected);
        long actual = phoneInformation.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPhoneInformationId() {
        String expected = "1234567";
        phoneInformation.setPhoneInformationId(expected);
        String actual = phoneInformation.getPhoneInformationId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetLocation() {
        String expected = "Location";
        phoneInformation.setLocation(expected);
        String actual = phoneInformation.getLocation();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPhoneNumber() {
        String expected = "PhoneNumber";
        phoneInformation.setPhoneNumber(expected);
        String actual = phoneInformation.getPhoneNumber();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPhoneType() {
        String expected = "PhoneType";
        phoneInformation.setPhoneType(expected);
        String actual = phoneInformation.getPhoneType();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetCustomLocation() {
        String expected = "CustomLocation";
        phoneInformation.setCustomLocation(expected);
        String actual = phoneInformation.getCustomLocation();
        Assert.assertEquals(expected, actual);
    }
}

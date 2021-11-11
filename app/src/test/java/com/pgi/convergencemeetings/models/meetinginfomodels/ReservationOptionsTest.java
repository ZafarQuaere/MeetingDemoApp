package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.TestLogger;
import com.pgi.convergencemeetings.base.ConvergenceApplication;
import com.pgi.convergencemeetings.models.ReservationOptions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.rule.PowerMockRule;

import java.util.Random;

/**
 * Created by ashwanikumar on 8/22/2017.
 */

public class ReservationOptionsTest extends RobolectricTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private ReservationOptions reservationOptions;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ConvergenceApplication.mLogger = new TestLogger();
        reservationOptions = new ReservationOptions(0L, null);
    }

    @Test
    public void testGetId(){
        Random random = new Random();
        long expected = random.nextLong();
        reservationOptions.setId(expected);
        long actual = reservationOptions.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetReservationOptionsId(){
        String expected = "Dummy Reservation Options";
        reservationOptions.setReservationOptionsId(expected);
        String actual = reservationOptions.getReservationOptionsId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParemetarizedConstructor(){
        Random random = new Random();
        long expectedId = random.nextLong();
        String expectedOptions = "Dummy Reservation Options";
        reservationOptions = new ReservationOptions(expectedId, expectedOptions);
        long actualId = reservationOptions.getId();
        String actualOptions = reservationOptions.getReservationOptionsId();
        Assert.assertEquals(expectedId, actualId);
        Assert.assertEquals(expectedOptions, actualOptions);
    }
}

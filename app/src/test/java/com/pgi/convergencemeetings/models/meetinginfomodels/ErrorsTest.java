package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.Errors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class ErrorsTest extends RobolectricTest {

    private Errors errors;

    @Before
    public void setUp() throws Exception {
        errors = new Errors(0L, null);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        errors.setId(expected);
        long actual = errors.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetErrorsId() {
        String expected = "errorsId";
        errors.setErrorsId(expected);
        String actual = errors.getErrorsId();
        Assert.assertEquals(expected, actual);
    }
}

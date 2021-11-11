package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.Presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class PresenterTest extends RobolectricTest {

    private Presenter presenter;

    @Before
    public void setUp() {
        presenter = new Presenter(0L, null, true, true, null, true, true, false);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        presenter.setId(expected);
        long actual = presenter.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPackagesId() {
        String expected = "1234567";
        presenter.setPresentersId(expected);
        String actual = presenter.getPresentersId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetEmail() {
        String expected = "1234567";
        presenter.setEmail(expected);
        String actual = presenter.getEmail();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetApplied() {
        presenter.setApplied(true);
        boolean actual = presenter.getApplied();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetAttended() {
        presenter.setAttended(true);
        boolean actual = presenter.getAttended();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetInvited() {
        presenter.setInvited(true);
        boolean actual = presenter.getInvited();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetModerator() {
        presenter.setModerator(true);
        boolean actual = presenter.getModerator();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetRegistered() {
        presenter.setRegistered(true);
        boolean actual = presenter.getRegistered();
        Assert.assertTrue(actual);
    }
}

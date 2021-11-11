package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.Feature;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class FeatureTest extends RobolectricTest {

    private Feature feature;

    @Before
    public void setUp() throws Exception {
        feature = new Feature(0L, null, null,null, null);
    }

    @Test
    public void testGetId(){
        Random random = new Random();
        long expected = random.nextLong();
        feature.setId(expected);
        long actual = feature.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetConferenceOptionId(){
        String expected = "featuresId";
        feature.setFeaturesId(expected);
        String actual = feature.getFeaturesId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDescription(){
        String expected = "testGetDescription";
        feature.setDescription(expected);
        String actual = feature.getDescription();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetFeatureCode(){
        String expected = "FeatureCode";
        feature.setFeatureCode(expected);
        String actual = feature.getFeatureCode();
        Assert.assertEquals(expected, actual);
    }
}

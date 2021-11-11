package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.Feature;
import com.pgi.convergencemeetings.models.FeatureData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class FeatureDataTest extends RobolectricTest {

    private FeatureData featureData;

    @Before
    public void setUp() throws Exception {
        featureData = new FeatureData(0L, null);
    }

    @Test
    public void testGetId(){
        Random random = new Random();
        long expected = random.nextLong();
        featureData.setId(expected);
        long actual = featureData.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetFeaturesId(){
        String expected = "featureId";
        featureData.setFeatureDataId(expected);
        String actual = featureData.getFeatureDataId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetFeatures(){
        Random random = new Random();
        long featureId = random.nextLong();
        List<Feature> expected = new ArrayList<>();
        expected.add(new Feature(featureId, null, null, null, null));
        featureData.setFeatures(expected);
        Feature actual = featureData.getFeatures().get(0);
        Assert.assertEquals(featureId, (long)actual.getId());
    }
}

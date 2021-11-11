package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.FeatureData;
import com.pgi.convergencemeetings.models.Product;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by amit1829 on 8/23/2017.
 */

public class ProductTest extends RobolectricTest {

    private Product product;

    @Before
    public void setUp() throws Exception {
        product = new Product(0L, null, null, null, null, null, false, false, true);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        product.setId(expected);
        long actual = product.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPackagesId() {
        String expected = "1234567";
        product.setFeatureDataId(expected);
        String actual = product.getFeatureDataId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetFeatureData() {
        Random random = new Random();
        long expectedId = random.nextLong();
        FeatureData expected = new FeatureData();
        expected.setId(expectedId);
        product.setFeatureData(expected);
        FeatureData actual = product.getFeatureData();
        Assert.assertEquals(expected.getId(), actual.getId());
    }

    @Test
    public void testGetDescription() {
        String expected = "Description";
        product.setDescription(expected);
        String actual = product.getDescription();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetName() {
        String expected = "Name";
        product.setName(expected);
        String actual = product.getName();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetProductCode() {
        String expected = "ProductCode";
        product.setProductCode(expected);
        String actual = product.getProductCode();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetHasAudioConf() {
        product.setHasAudioConf(true);
        boolean actual = product.getHasAudioConf();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetHasWebConf() {
        product.setHasWebConf(true);
        boolean actual = product.getHasWebConf();
        Assert.assertTrue(actual);
    }

    @Test
    public void testGetHasWebinar() {
        product.setHasWebinar(true);
        boolean actual = product.getHasWebinar();
        Assert.assertTrue(actual);
    }
}

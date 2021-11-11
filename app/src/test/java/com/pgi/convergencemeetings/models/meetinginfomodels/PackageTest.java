package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.Package;
import com.pgi.convergencemeetings.models.ProductData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by amit1829 on 8/22/2017.
 */

public class PackageTest extends RobolectricTest {

    private Package aPackage;

    @Before
    public void setUp() throws Exception {
        aPackage = new Package(0L, null, null, null, null, null);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        aPackage.setId(expected);
        long actual = aPackage.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPackagesId() {
        String expected = "1234567";
        aPackage.setPackagesId(expected);
        String actual = aPackage.getPackagesId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetProductDataId() {
        String expected = "1234567";
        aPackage.setProductDataId(expected);
        String actual = aPackage.getProductDataId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetDescription() {
        String expected = "Description";
        aPackage.setDescription(expected);
        String actual = aPackage.getDescription();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetName() {
        String expected = "Name";
        aPackage.setName(expected);
        String actual = aPackage.getName();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetPackageCode() {
        String expected = "PackageCode";
        aPackage.setPackageCode(expected);
        String actual = aPackage.getPackageCode();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetProductData() {
        Random random = new Random();
        long expectedId = random.nextLong();
        ProductData productData = new ProductData();
        productData.setId(expectedId);
        aPackage.setProductData(productData);
        Assert.assertEquals(expectedId, (long)aPackage.getProductData().getId());
    }
}

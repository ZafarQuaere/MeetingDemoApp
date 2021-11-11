package com.pgi.convergencemeetings.models.meetinginfomodels;

import com.pgi.convergencemeetings.RobolectricTest;
import com.pgi.convergencemeetings.models.Product;
import com.pgi.convergencemeetings.models.ProductData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by amit1829 on 8/23/2017.
 */

public class ProductDataTest extends RobolectricTest {
    private ProductData productData;

    @Before
    public void setUp() throws Exception {
        productData = new ProductData(0L, null);
    }

    @Test
    public void testGetId() {
        Random random = new Random();
        long expected = random.nextLong();
        productData.setId(expected);
        long actual = productData.getId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetProductDataId() {
        String expected = "1234567";
        productData.setProductDataId(expected);
        String actual = productData.getProductDataId();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testGetConferenceOptions() {
        Random random = new Random();
        long expectedId = random.nextLong();
        List<Product> expected = new ArrayList<>();
        Product product = new Product();
        product.setId(expectedId);
        expected.add(product);
        productData.setProducts(expected);
        Product actual = productData.getProducts().get(0);
        Assert.assertEquals(expectedId, (long) actual.getId());
    }
}

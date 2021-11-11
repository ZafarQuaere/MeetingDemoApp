package org.mockito.configuration;

/**
 * Created by amit1829 on 8/21/2017.
 */

public class MockitoConfiguration extends DefaultMockitoConfiguration {

    @Override
    public boolean enableClassCache() {
        return false;
    }
}

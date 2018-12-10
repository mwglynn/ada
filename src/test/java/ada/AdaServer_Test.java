package ada;

import ada.postgresql.AdaDB;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AdaServer_Test {

    private AdaDB testDb;

    @Before
    public void setUp() {
        testDb = new AdaDB("localhost",
                "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortLowFails() {
        AdaServer host = new AdaServer(0,
                testDb);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortHighFails() {
        AdaServer host = new AdaServer(65536,
                testDb);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortNegativeFails() {
        AdaServer host = new AdaServer(-1,
                testDb);
    }

    @Test
    public void testConnectManyHostsSucceeds() {
        for (int i = 9090;
             i < 9190;
             i++) {
            AdaServer host = new AdaServer(i,
                    testDb);
            Assert.assertNotNull(host);
        }
    }

    @After
    public void tearDown() {
        testDb.clear();
    }

}

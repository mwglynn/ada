package ada;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TCPHost_Test {

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortLowFails() {
        TCPHost host = new TCPHost(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortHighFails() {
        TCPHost host = new TCPHost(65536);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortNegativeFails() {
        TCPHost host = new TCPHost(-1);
    }

    @Test
    public void testConnectManyHostsSucceeds() {
        for (int i = 9090; i < 9190; i++) {
            TCPHost host = new TCPHost(i);
            Assert.assertNotNull(host);
        }
    }
}

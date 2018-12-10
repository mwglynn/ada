package ada;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NetworkSocketClient_Test {

    @Test
    public void unknownHostSucceeds() {
        new NetworkSocketClient("9999.9999999.0.0",
                9090);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortHighFails() {
        new NetworkSocketClient("localhost",
                65536);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortLowFails() {
        new NetworkSocketClient("localhost",
                0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortNegativeFails() {
        new NetworkSocketClient("localhost",
                -1);
    }
}

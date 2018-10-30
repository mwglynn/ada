import java.io.*;
import org.junit.Assert;
import org.junit.Test;

public class NetworkSocketClient_Test {

    @Test
    public void unknownHost() {
        NetworkSocketClient client = new NetworkSocketClient("9999.9999999.0.0", 9090);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortHigh() {
        NetworkSocketClient client = new NetworkSocketClient("localhost", 65536);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortLow() {
        NetworkSocketClient client = new NetworkSocketClient("localhost", 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortNegative() {
        NetworkSocketClient client = new NetworkSocketClient("localhost", -1);
    }

}

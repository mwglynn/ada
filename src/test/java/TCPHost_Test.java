import org.junit.Test;
import org.junit.Assert;

public class TCPHost_Test {

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortLow() {
        TCPHost host = new TCPHost(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortHigh() {
        TCPHost host = new TCPHost(65536);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgumentPortNegative() {
        TCPHost host = new TCPHost(-1);
    }

    @Test
    public void manyHosts() {
        for (int i = 9090; i < 9190; i++) {
            TCPHost host = new TCPHost(i);
            Assert.assertNotNull(host);
        }
    }


}

import org.junit.Assert;
import org.junit.Test;

public class TCPMessageReader_Test {
    @Test
    public void nullTest() {

        TCPSocketClient client = new TCPSocketClient("localhost", 6259);
        TCPMessageReader reader = new TCPMessageReader(client);

        String s = new String("should be null!");
        Assert.assertNotNull(s);

        s = reader.ReadMessage();
        Assert.assertNull(s);

    }
}
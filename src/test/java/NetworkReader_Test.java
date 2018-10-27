import org.junit.Assert;
import org.junit.Test;

public class NetworkReader_Test {
    @Test
    public void nullTest() {

        NetworkSocketClient client = new NetworkSocketClient("localhost", 6259);
        NetworkReader reader = new NetworkReader(client);

        String s = new String("should be null!");
        Assert.assertNotNull(s);

        s = reader.ReadMessage();
        Assert.assertNull(s);

    }
}
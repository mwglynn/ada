package ada;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.net.UnknownHostException;

@RunWith(JUnit4.class)
public class NetworkSocketClient_Test {

  @Test(expected = UnknownHostException.class)
  public void unknownHostFails() throws IOException {
    new NetworkSocketClient("9999.9999999.0.0", 9090);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalArgumentPortHighFails() throws IOException {
    new NetworkSocketClient("localhost", 65536);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalArgumentPortLowFails() throws IOException {
    new NetworkSocketClient("localhost", 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalArgumentPortNegativeFails() throws IOException {
    new NetworkSocketClient("localhost", -1);
  }
}

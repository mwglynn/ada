import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Optional;

@RunWith(JUnit4.class)
public class Messages_inputEmoji_Test {

    private static final int PORT = 6259;
    private static final String EMOJI_STRING = "\uD83D\uDE0A";

    @SuppressWarnings("StatementWithEmptyBody")
    @Test
    public void SendMessages_WithEmoji_Fails() {
        TCPHost host = new TCPHost(PORT);

        Thread hostThread =
                new Thread(
                        () -> {
                            while (host.Tick()) {
                            }
                            host.Close();
                        });
        hostThread.start();

        NetworkSocketClient client1 = new NetworkSocketClient("localhost", PORT);
        NetworkSender sender1 = new NetworkSender(client1);
        NetworkSocketClient client2 = new NetworkSocketClient("localhost", PORT);
        NetworkReader reader2 = new NetworkReader(client2);


        sender1.SendMessage(EMOJI_STRING);

        Optional<String> s2 = reader2.ReadMessage();
        while (!s2.isPresent()) {
            s2 = reader2.ReadMessage();
        }
        Assert.assertNotEquals(EMOJI_STRING, s2.get());

        /* clean exit */
        sender1.Close();
        reader2.Close();
        hostThread.interrupt();
    }
}

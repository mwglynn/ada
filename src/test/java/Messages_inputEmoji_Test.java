import org.junit.Test;
import org.junit.Assert;
import java.util.Optional;

public class Messages_inputEmoji_Test {

    private static final int port = 6259;

    @SuppressWarnings("StatementWithEmptyBody")
    @Test
    public void messagesemoji() {
        TCPHost host = new TCPHost(port);

        Thread hostThread =
                new Thread(
                        () -> {
                            while (host.Tick()) {}
                            host.Close();
                        });
        hostThread.start();

        NetworkSocketClient client1 = new NetworkSocketClient("localhost", port);
        NetworkSender sender1 = new NetworkSender(client1);
        NetworkSocketClient client2 = new NetworkSocketClient("localhost", port);
        NetworkReader reader2 = new NetworkReader(client2);


        Thread sendMessages =
                new Thread(
                        () -> {
                            while (!Thread.interrupted()) {
                                sender1.SendMessage("\uD83D\uDE0A");
                            }
                        });
        sendMessages.start();

        Optional<String> s2;
        do {
            s2 = reader2.ReadMessage();
            if (s2.isPresent()) {
                System.out.println("-" + s2.get());
                /* equals converts */
                Assert.assertNotEquals("\uD83D\uDE0A", s2.get());
                break;
            }
        } while (true);

        /* clean exit */
        sender1.Close();
        reader2.Close();

        sendMessages.interrupt();
        hostThread.interrupt();
//        System.exit(0);

    }
}

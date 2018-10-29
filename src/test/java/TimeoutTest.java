import org.junit.Assert;
import org.junit.Test;
import java.util.concurrent.TimeUnit;

/**
 * Checks equivalence test of long-running server.
 * - sets up server and client
 * - sends initial message
 * - sits for 5 seconds
 * - exits
 */
public class TimeoutTest {

    @Test
    public void nullTest() {

        TCPHost host = new TCPHost(6259);

        Thread hostThread = new Thread(() -> {
            while (host.Tick());
            host.Close();
        });
        hostThread.start();

        NetworkSocketClient client = new NetworkSocketClient("localhost", 6259);
        NetworkSender sender = new NetworkSender(client);
        NetworkReader reader = new NetworkReader(client);

        sender.SendMessage("simple quick message");

        String s = null;
        while (s == null) {
            s = reader.ReadMessage();
        }
        System.out.println("--------------sleeping for 5 seconds--------------");

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch(InterruptedException e) {
            System.out.println("got interrupted!");
        }

        /* quitting message, terminates server */
        sender.SendMessage("\\q");

        /* clean exit */
        sender.Close();
        reader.Close();
        client.Close();
        try {
            hostThread.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }




}

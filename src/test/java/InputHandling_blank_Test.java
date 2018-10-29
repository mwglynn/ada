import org.junit.Test;


public class InputHandling_blank_Test {

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

        sender.SendMessage(""); // ==> no message

        String s = null;
        while (s == null) {
            s = reader.ReadMessage();
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

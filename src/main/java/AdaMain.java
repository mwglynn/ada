/**
 * Main class, connecting client and server.
 */
public class AdaMain {
    /**
     * simple create, send, destroy.
     * create host with random port
     * client sits on 127.0.0.1
     * @param args --will be used later?
     */
    public static void main(String[] args) {
        TCPHost host = new TCPHost(6259);

        Thread hostThread = new Thread(() -> {
            while (host.Tick());
            host.Close();
        });
        hostThread.start();

        NetworkSocketClient client = new NetworkSocketClient("localhost", 6259);
        NetworkSender sender = new NetworkSender(client);
        NetworkReader reader = new NetworkReader(client);

        sender.SendMessage("Test");

        String s = null;
        while (s == null) {
            s = reader.ReadMessage();
        }

        /* this is where we can save to database too */
        System.out.println(s);

        /* audio recording thanks for google text-to-speech */
        /* uncomment me to pull in audio file */
//        Speaker speak = new Speaker();
//        speak.ReadMessage(s);


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

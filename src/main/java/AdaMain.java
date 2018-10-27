public class AdaMain {

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
        sender.SendMessage("\\q");
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
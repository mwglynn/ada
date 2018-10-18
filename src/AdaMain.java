
public class AdaMain {

    public static void main(String[] args) {
        TCPSocketClient client = new TCPSocketClient("localhost", 6259);
        TCPMessageSender sender = new TCPMessageSender(client);
        TCPMessageReader reader = new TCPMessageReader(client);
        sender.SendMessage("Test");

        String s = null;
        while (s == null) {
            s = reader.ReadMessage();
        }
        System.out.println(s);
        sender.Close();
        reader.Close();
        client.Close();
    }
}
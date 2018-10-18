
public class AdaMain {

    public static void main(String[] args) {
        TCPSocketClient client = new TCPSocketClient("localhost", 6259);
        TCPMessageSender sender = new TCPMessageSender(client);
        sender.SendMessage("Test");
        sender.Close();
        client.Close();
    }
}
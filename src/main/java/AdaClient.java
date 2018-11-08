import java.util.Optional;
import java.util.Scanner;

/**
 * {@code AdaClient} reads messages from and sends Messages to {@link AdaServer}.
 */
@SuppressWarnings("WeakerAccess")
public class AdaClient {

    private static final int PORT = 6259;

    public static void main(String[] args) {
        NetworkSocketClient client = new NetworkSocketClient("localhost", PORT);
        NetworkSender sender = new NetworkSender(client);
        NetworkReader reader = new NetworkReader(client);
        Scanner input = new Scanner(System.in);

        Thread sendMessages =
                new Thread(
                        () -> {
                            while (!Thread.interrupted()) {
                                if (input.hasNext()) {
                                    sender.SendMessage(input.nextLine());
                                } else {
                                    input.nextLine();
                                }
                            }
                        });
        sendMessages.start();

        Optional<String> s;
        do {
            s = reader.ReadMessage();
            if (s.isPresent()) {
                System.out.println("-" + s.get());
                if (s.get().equals("exit")) {
                    break;
                }
            }
        } while (true);

        System.out.println("closing out");

        /* clean exit */
        sender.Close();
        reader.Close();
        client.Close();

        try {
            sendMessages.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

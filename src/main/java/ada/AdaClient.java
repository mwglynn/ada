package ada;

import java.util.Optional;
import java.util.Scanner;
import org.json.*;

/**
 * {@code ada.AdaClient} reads messages from and sends Messages to {@link AdaServer}.
 */
@SuppressWarnings("WeakerAccess")
public class AdaClient {

    private static final int PORT = 6259;

    public static void main(String[] args) {
        NetworkSocketClient client = new NetworkSocketClient("localhost", PORT);
        NetworkSender sender = new NetworkSender(client);
        NetworkReader reader = new NetworkReader(client);
        Scanner input = new Scanner(System.in);

        System.out.print("Please enter a username: ");
        String username = input.nextLine();

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
        sender.SendMessage("\\username " + username);
        Optional<String> s;
        do {
            s = reader.ReadMessage();
            if (s.isPresent()) {
                JSONObject jobj = new JSONObject(s.get());
                String parsedSender = jobj.getString("sender");
                String parsedMsg = jobj.getString("msg");
                System.out.println(parsedSender + ": " + parsedMsg);
                if (parsedMsg.equals("exit")) {
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

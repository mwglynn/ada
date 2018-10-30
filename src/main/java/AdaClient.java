import java.util.Scanner;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/** {@code AdaClient} reads messages from and sends Messages to {@link AdaServer}. */
public class AdaClient {

    private static final int port = 6259;

    public static void main(String[] args) {
        NetworkSocketClient client = new NetworkSocketClient("localhost", port);
        NetworkSender sender = new NetworkSender(client);
        NetworkReader reader = new NetworkReader(client);
        Scanner input = new Scanner(System.in);

        Thread sendMessages = new Thread(() -> {
            while (true) {
                if (input.hasNext()) {
                    sender.SendMessage(input.nextLine());
                } else {
                    input.nextLine();
                }
            }      
        });
        sendMessages.start();
        
        String s = null;
        do {
            s = reader.ReadMessage();
            if (s != null) {
                System.out.println("-" + s);
                if(s.equals("exit")) {
                    break;
                }
            }
        } while (true);


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

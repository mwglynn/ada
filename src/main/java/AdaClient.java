import java.util.Optional;
import java.util.Scanner;

/** {@code AdaClient} reads messages from and sends Messages to {@link AdaServer}. */
public class AdaClient {

  private static final int port = 6259;

  public static void main(String[] args) {
    NetworkSocketClient client = new NetworkSocketClient("localhost", port);
    NetworkSender sender = new NetworkSender(client);
    NetworkReader reader = new NetworkReader(client);
    Scanner input = new Scanner(System.in);

    Thread sendMessages =
        new Thread(
            () -> {
              while (true) {
                if (input.hasNext()) {
                  sender.SendMessage(input.nextLine());
                } else {
                  input.nextLine();
                }
              }
            });
    sendMessages.start();

    Optional<String> s = Optional.empty();
    do {
      s = reader.ReadMessage();
      if (s.isPresent()) {
        System.out.println("-" + s.get());
        if (s.get().equals("exit")) {
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

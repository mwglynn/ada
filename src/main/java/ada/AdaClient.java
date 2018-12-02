package ada;

import ada.texttospeech.AdaTextToSpeechClient;
import ada.texttospeech.AudioUtil;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

/** {@code ada.AdaClient} reads messages from and sends Messages to {@link AdaServer}. */
@SuppressWarnings("WeakerAccess")
public class AdaClient {

    private static final int PORT = 6259;

    /** Main method for the AdaClient. */
    public static void main(String[] args) {

        AdaTextToSpeechClient textToSpeechClient;
        try {
            textToSpeechClient = new AdaTextToSpeechClient(TextToSpeechClient.create());
        } catch (IOException e) {
            textToSpeechClient = null;
        }
        String host = args.length > 0 ? args[0] : "localhost";
    NetworkSocketClient client = new NetworkSocketClient(host, PORT);
    NetworkSender sender = new NetworkSender(client);
    NetworkReader reader = new NetworkReader(client);
    Scanner input = new Scanner(System.in);

    /* DB: log user if new */
        String username;
        String answer;
        String flag;

    /**
     * DB: known bug - multiple logins same person allowed this is not fully a bug because no two
     * people can have the same username, so someone would have to lie and say that they do have an
     * account when the really do not and then use an existing username as their own reach goal:
     * implement authorization mechanism
     */
    while (true) {
      System.out.print("Do you already have an account (y/n):  ");
      answer = input.nextLine();
      if (answer.equals("n")) {
        flag = "n";
        System.out.print("Please enter a username: ");
        username = input.nextLine();
        Boolean ret = PostgreSQL_createUser.Create(host, username, flag);
        if (ret.equals(false)) {
          System.out.println("please try again!");
        } else {
          System.out.println("user created in database!");
          break;
        }
      } else if (answer.equals("y")) {
        flag = "y";
        System.out.print("Please enter *your* username: ");
        /* check if in system */
        username = input.nextLine();
        new PostgreSQL_createUser();
        Boolean ret = PostgreSQL_createUser.checkUser(host, username, flag);
        if (ret.equals(true)) {
          System.out.println("username validated");
          break;
        } else {
          System.out.println("username not in system, try again");
        }
      } else {
        System.out.println("incorrect selection, please try again!");
      }
    }

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
    Optional<String> message;
    do {
      message = reader.ReadMessage();
      if (message.isPresent()) {
        JSONObject jobj = new JSONObject(message.get());
        String parsedSender = jobj.getString("sender");
        String parsedMsg = jobj.getString("msg");

        System.out.println(parsedSender + ": " + parsedMsg);
          if (textToSpeechClient != null) {
              textToSpeechClient.getAudio(parsedMsg).ifPresent(AudioUtil::play);
          }
        /* DB: SQL insertion */
        PostgreSQL_insertChat.Insert(host, jobj, username);

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

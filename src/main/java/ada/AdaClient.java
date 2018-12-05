package ada;

import ada.postgresql.AdaDB;
import ada.texttospeech.AdaTextToSpeechClient;
import ada.texttospeech.AudioUtil;
import org.json.JSONObject;

import java.util.Optional;
import java.util.Scanner;

/**
 * {@code ada.AdaClient} reads messages from and sends Messages to
 * {@link AdaServer}.
 */
@SuppressWarnings("WeakerAccess")
public class AdaClient {

    private final String host;
    private final AdaTextToSpeechClient textToSpeechClient;
    private final NetworkSocketClient client;
    private final NetworkSender sender;
    private final NetworkReader reader;
    private final Scanner input = new Scanner(System.in);
    private final AdaDB adaDB;

    public AdaClient(
            String host, AdaTextToSpeechClient textToSpeechClient,
            NetworkSocketClient client) {
        this.host = host;
        this.textToSpeechClient = textToSpeechClient;
        this.client = client;
        sender = new NetworkSender(client);
        reader = new NetworkReader(client);
        adaDB = new AdaDB(host, "ada");
    }

    public void run() {

        /* DB: log user if new */
        String username;
        /*
         * DB: known bug - multiple logins same person allowed this is not
         * fully a bug because no two
         * people can have the same username, so someone would have to lie
         * and say that they do have an
         * account when the really do not and then use an existing username
         * as their own reach goal:
         * implement authorization mechanism
         */
        label:
        while (true) {
            System.out.print("Do you already have an account (y/n):  ");
            switch (input.nextLine()) {
                case "n": {
                    System.out.print("Please enter a username: ");
                    username = input.nextLine();
                    if (adaDB.createUser(username)) {
                        System.out.println("user created in database!");
                        break label;
                    } else {
                        System.out.println("please try again!");
                    }
                    break;
                }
                case "y": {
                    System.out.print("Please enter *your* username: ");
                    /* check if in system */
                    username = input.nextLine();
                    if (adaDB.checkUser(username)) {
                        System.out.println("username validated");
                        break label;
                    } else {
                        System.out.println("username not in system, try again");
                    }
                    break;
                }
                default:
                    System.out.println("incorrect selection, please try " +
                            "again!");
                    break;
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

        getMessages(username);

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

    private void getMessages(final String username) {
        do {
            Optional<JSONObject> message =
                    reader.ReadMessage().flatMap(m -> Optional.of(new JSONObject(m)));
            if (message.isPresent()) {
                String parsedSender = message.get().getString("sender");
                String parsedMsg = message.get().getString("msg");

                // TODO: Not let clients close other clients.
                if (parsedMsg.equals("exit")) {
                    return;
                }
                System.out.println(parsedSender + ": " + parsedMsg);
                if (textToSpeechClient != null) {
                    textToSpeechClient.getAudio(parsedMsg).ifPresent(AudioUtil::play);
                }
                adaDB.insert(message.get(), username);
            }
        } while (true);
    }
}

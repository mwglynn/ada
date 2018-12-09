package ada;

import ada.postgresql.AdaDB;
import ada.texttospeech.AdaTextToSpeechClient;
import ada.texttospeech.AudioUtil;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

/**
 * {@code ada.AdaClient} reads messages from and sends Messages to
 * {@link AdaServer}.
 */
@SuppressWarnings("WeakerAccess")
public class AdaClient implements Closeable {

    private final AdaTextToSpeechClient textToSpeechClient;
    private final NetworkSender sender;
    private final NetworkReader reader;
    private final Scanner input;
    private final AdaDB adaDB;
    private Thread sendMessages;

    public AdaClient(
            AdaDB db,
            AdaTextToSpeechClient textToSpeechClient,
            NetworkSocketClient client) {
        adaDB = db;
        reader = new AdaNetworkReader(client);
        sender = new AdaNetworkSender(client);
        this.textToSpeechClient = textToSpeechClient;
        input = new Scanner(System.in);
    }


    public AdaClient(
            AdaDB db,
            AdaTextToSpeechClient textToSpeechClient,
            NetworkSocketClient client,
            InputStream in) {
        adaDB = db;
        reader = new AdaNetworkReader(client);
        sender = new AdaNetworkSender(client);
        this.textToSpeechClient = textToSpeechClient;
        input = new Scanner(System.in);
    }

    public void run() {
        String username = getUsername();
        sender.SendMessage("\\username " + username);
        getMessages(username);

        sendMessages =
                new Thread(
                        () -> {
                            while (!Thread.interrupted()) {
                                if (input.hasNext()) {
                                    String message = input.nextLine();
                                    if (message.equals(":exit:")) {
                                        System.exit(1);
                                    }
                                    sender.SendMessage(message);
                                } else {
                                    input.nextLine();
                                }
                            }
                        });
        sendMessages.start();
    }

    String getUsername() {
        while (true) {
            String usernameProvided;
            System.out.println("Do you have a username (y/n)?");
            switch (input.nextLine()) {
                case "y":
                    System.out.println("Enter *your* username here!");
                    usernameProvided = input.nextLine();
                    if (!adaDB.userExists(usernameProvided)) {
                        System.out.println(String.format("I have no idea who " +
                                        "this %s is. Try again!",
                                usernameProvided));
                    } else {
                        return usernameProvided;
                    }
                    break;
                case "n":
                    System.out.println("Enter a username here!");
                    usernameProvided = input.nextLine();
                    if (adaDB.createUser(usernameProvided)) {
                        return usernameProvided;
                    }
                    break;
                default:
                    System.out.println("Please enter y or n.");
            }
        }
    }

    void getMessages(final String username) {
        do {
            Optional<JSONObject> message =
                    reader.ReadMessage()
                            .flatMap(m -> Optional.of(new JSONObject(m)));
            if (message.isPresent()) {
                String parsedSender = message.get()
                        .getString("sender");
                String parsedMsg = message.get()
                        .getString("msg");

                // TODO: Not let clients close other clients.
                if (parsedMsg.equals("exit")) {
                    return;
                }
                System.out.println(parsedSender + ": " + parsedMsg);
                if (textToSpeechClient != null) {
                    textToSpeechClient.getAudio(parsedMsg)
                            .ifPresent(AudioUtil::play);
                }
                adaDB.insert(message.get(),
                        username);
            }
        } while (true);
    }

    @Override
    public void close() {
        sender.close();
        reader.close();

        try {
            sendMessages.join();
        } catch (
                InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

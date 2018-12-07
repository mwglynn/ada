package ada;

import ada.postgresql.AdaDB;
import ada.texttospeech.AdaTextToSpeechClient;
import ada.texttospeech.AudioUtil;
import org.json.JSONObject;

import java.io.Closeable;
import java.util.Optional;
import java.util.Scanner;

/**
 * {@code ada.AdaClient} reads messages from and sends Messages to
 * {@link AdaServer}.
 */
@SuppressWarnings("WeakerAccess")
public class AdaClient implements Closeable {

    private final AdaTextToSpeechClient textToSpeechClient;
    private final NetworkSocketClient client;
    private final NetworkSender sender;
    private final NetworkReader reader;
    private final Scanner input = new Scanner(System.in);
    private final AdaDB adaDB;
    private Thread sendMessages;

    public AdaClient(
            String host, AdaTextToSpeechClient textToSpeechClient,
            NetworkSocketClient client) {
        this.textToSpeechClient = textToSpeechClient;
        this.client = client;
        sender = new NetworkSender(client);
        reader = new NetworkReader(client);
        adaDB = new AdaDB(host, "ada");
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
                                        return;
                                    }
                                    sender.SendMessage(input.nextLine());
                                } else {
                                    input.nextLine();
                                }
                            }
                        });
        sendMessages.start();


        System.out.println("closing out");

        /* clean exit */
        sender.close();
        reader.close();
        client.close();


    }

    private String getUsername() {
        while (true) {
            String usernameProvided;
            System.out.println("Do you have a username (y/n)?");
            switch (input.nextLine()) {
                case "y":
                    System.out.println("Enter *your* username here!");
                    usernameProvided = input.nextLine();
                    if (!adaDB.userExists(usernameProvided)) {
                        System.out.println(String.format("I have no idea who " +
                                "this %s is", usernameProvided));
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

    @Override
    public void close() {
        sender.close();
        reader.close();
        client.close();

        try {
            sendMessages.join();
        } catch (
                InterruptedException ie) {
            ie.printStackTrace();
        }

    }
}

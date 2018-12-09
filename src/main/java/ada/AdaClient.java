package ada;

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

    private final AdaTextToSpeechClient textToSpeechClient;
    private final NetworkSender sender;
    private final NetworkReader reader;
    private final Scanner input = new Scanner(System.in);
    private String username;

    public AdaClient(
            AdaTextToSpeechClient textToSpeechClient,
            NetworkSocketClient client) {
        this.textToSpeechClient = textToSpeechClient;
        sender = new NetworkSender(client);
        reader = new NetworkReader(client);
    }


    public void run() {

        /* DB: log user if new */

        UsernameRequest request = getUsername();
        System.out.println("Got username " + request.username());
        sender.SendMessage(request.serialize());
        while (username == null) {
            Optional<String> msg = reader.ReadMessage();
            if (msg.isPresent()) {
                try {
                    UsernameResponse response =
                            UsernameResponse.deserialize(msg.get());
                    if (response.usernameWasReceived()) {
                        username = request.username();
                    } else {
                        System.out.println("Try again!");
                        request = getUsername();
                        sender.SendMessage(request.serialize());
                    }
                } catch (Exception e) {
                    System.out.println("Try again!");
                    request = getUsername();
                    sender.SendMessage(request.serialize());
                }
            }
        }
        System.out.println(username + " is no longer null!");

        System.out.println("Welcome to Ada! Type into the prompt to chat! To " +
                "see your chat history, type :history:");

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

        getMessages(username);

        System.out.println("closing out");

        /* clean exit */
        sender.Close();
        reader.Close();

        try {
            sendMessages.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private UsernameRequest getUsername() {
        while (true) {
            System.out.println("Do you have a username?");
            String hasUsername = input.nextLine();
            switch (hasUsername) {
                case "n": {
                    System.out.print("Please enter a username: ");
                    return UsernameRequest.create(input.nextLine(),
                            false);
                }
                case "y": {
                    System.out.print("Please enter *your* username: ");
                    return UsernameRequest.create(input.nextLine(),
                            true);
                }
                default:
                    System.out.println("Invalid selection; please try " +
                            "again!");
            }
        }
    }

    private void getMessages(final String username) {
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
            }
        } while (true);
    }

    public void close() {
        sender.Close();
        reader.Close();
    }
}

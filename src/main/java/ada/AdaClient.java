package ada;

import ada.texttospeech.AdaTextToSpeechClient;
import ada.texttospeech.AudioUtil;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.InputStream;
import java.util.Optional;
import java.util.Scanner;

/**
 * {@code ada.AdaClient} reads messages from and sends Messages to
 * {@link AdaServerMain}.
 */
@SuppressWarnings("WeakerAccess")
public class AdaClient implements Closeable {

    private final AdaTextToSpeechClient textToSpeechClient;
    private final NetworkSender sender;
    private final NetworkReader reader;
    private final Scanner input;
    private String username;
    private volatile boolean keepSendingMessages = true;

    public AdaClient(
            AdaTextToSpeechClient textToSpeechClient,
            NetworkSocketClient client) {
        input = new Scanner(System.in);
        this.textToSpeechClient = textToSpeechClient;
        sender = new NetworkSender(client);
        reader = new NetworkReader(client);
    }

    public AdaClient(
            AdaTextToSpeechClient textToSpeechClient,
            NetworkSocketClient client,
            InputStream in) {
        input = new Scanner(in);
        this.textToSpeechClient = textToSpeechClient;
        sender = new NetworkSender(client);
        reader = new NetworkReader(client);
    }

    public void run() {

        /* Prompt for username. */
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
                                String next = input.nextLine();
                                if (next.equals("exit")) {
                                    keepSendingMessages = false;
                                    break;
                                }
                                if (next.length() > 3000) {
                                    System.out.println("Error: Line " +
                                            "length exceeded. Try " +
                                            "splitting your message!");
                                } else {
                                    System.out.println("Sending " + next);
                                    sender.SendMessage(next);
                                }
                            }
                        });

        sendMessages.start();

        getMessages();
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

    private void getMessages() {
        do {
            Optional<JSONObject> message =
                    reader.ReadMessage()
                            .flatMap(m -> Optional.of(new JSONObject(m)));
            if (message.isPresent()) {
                String parsedSender = message.get()
                        .getString("sender");
                String parsedMsg = message.get()
                        .getString("msg");

                System.out.println(parsedSender + ": " + parsedMsg);
                if (textToSpeechClient != null) {
                    textToSpeechClient.getAudio(parsedMsg)
                            .ifPresent(AudioUtil::play);
                }
            }
        } while (keepSendingMessages);
    }

    @Override
    public void close() {
        sender.close();
        reader.close();
    }
}

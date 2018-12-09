package ada;

import ada.texttospeech.AdaTextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;

import java.io.IOException;

@SuppressWarnings("WeakerAccess")
public class AdaClientMain {

    private static final int PORT = 6259;

    public static void main(String[] args) {
        String host = args.length > 0 ?
                args[0] :
                "localhost";
        AdaTextToSpeechClient cloudClient;

        try {
            cloudClient =
                    new AdaTextToSpeechClient(TextToSpeechClient.create());
        } catch (IOException e) {
            cloudClient = null;
        }
        AdaClient client = new AdaClient(cloudClient,
                new NetworkSocketClient(host,
                        PORT));
        client.run();
    }
}

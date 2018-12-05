package ada;

import ada.texttospeech.AdaTextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

@SuppressWarnings("WeakerAccess")
public class AdaClientMain {

  private static final int PORT = 6259;

  public static void main(String[] args) {
    AdaTextToSpeechClient cloudClient;
    try {
      cloudClient = new AdaTextToSpeechClient(TextToSpeechClient.create());
    } catch (IOException e) {
      cloudClient = null;
    }

    String host = args.length > 0 ? args[0] : "localhost";
      try {
          AdaClient client = new AdaClient(host, cloudClient, new NetworkSocketClient(host, PORT));
      client.run();
    } catch (ConnectException e) {
      System.out.println("Unable to connect to the server.");
      System.exit(1);
    } catch (UnknownHostException e) {
      System.out.println("Please use 127.0.0.1!");
      System.exit(1);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}

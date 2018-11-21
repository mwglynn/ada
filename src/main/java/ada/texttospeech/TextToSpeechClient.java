package ada.texttospeech;

// Imports the Google Cloud client library

import com.google.cloud.texttospeech.v1.*;

import java.io.IOException;

/**
 * Text to speech client class for getting audio (via the text to speech client). Branched from
 * Google Cloud TextToSpeech API sample application.
 */
public class TextToSpeechClient {
    private com.google.cloud.texttospeech.v1.TextToSpeechClient textToSpeechClient;
    private static final VoiceSelectionParams voice =
            VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.FEMALE)
                    .build();
    private final AudioConfig audioConfig =
            AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build();

    /**
     * Text to Speech Client for interfacing with Cloud TTS.
     */
    private TextToSpeechClient() throws IOException {
        textToSpeechClient = com.google.cloud.texttospeech.v1.TextToSpeechClient.create();
    }

    private Linear16Audio getAudio(@SuppressWarnings("SameParameterValue") String text) {
        SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
        SynthesizeSpeechResponse response =
                textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
        return new Linear16Audio(response.getAudioContent().toByteArray());
    }

    public static void main(String... args) throws Exception {
        TextToSpeechClient client = new TextToSpeechClient();
        client.getAudio("King Arthur!").play();
    }
}

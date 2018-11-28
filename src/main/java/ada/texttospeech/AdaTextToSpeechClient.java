package ada.texttospeech;

// Imports the Google Cloud client library

import com.google.cloud.texttospeech.v1.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

/**
 * Text to speech client class for getting audio (via the text to speech client). Branched from
 * Google Cloud TextToSpeech API sample application.
 */
public final class AdaTextToSpeechClient {
  private TextToSpeechClient client;
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
  public AdaTextToSpeechClient(TextToSpeechClient client) {
    this.client = client;
  }

  private SynthesizeSpeechResponse getAudioResponse(String text) {
    SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
    return client.synthesizeSpeech(input, voice, audioConfig);
  }

  public Optional<AudioInputStream> getAudio(String text) {
    try {
      return Optional.of(
              AudioSystem.getAudioInputStream(
                      new ByteArrayInputStream(getAudioResponse(text).getAudioContent().toByteArray())));
    } catch (UnsupportedAudioFileException | IOException | NullPointerException e) {
      System.out.println("Warning: " + e);
      return Optional.empty();
    }
  }
}

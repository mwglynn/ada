package ada.texttospeech;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.file.FileSystems;

@RunWith(JUnit4.class)
public class AudioUtilTest {

  private static AudioInputStream validAudio;

  @Before
  public void setUp() throws IOException, UnsupportedAudioFileException {
    validAudio =
            AudioSystem.getAudioInputStream(
                    FileSystems.getDefault()
                            .getPath("src", "test", "java", "ada", "texttospeech", "valid_audio.wav")
                            .toFile());
  }

  @Test
  public void play_invalidAudio_PlaysNothing() {
    System.out.println("This test should not play anything.");
    AudioUtil.play(null);
  }

  @Test
  public void play_validAudio_Succeeds() {
    System.out.println("Playing: \"King Arthur\"");
    AudioUtil.play(validAudio);
  }

  @After
  public void tearDown() throws IOException {
    validAudio.close();
  }
}

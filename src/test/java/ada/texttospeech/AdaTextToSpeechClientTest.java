package ada.texttospeech;

import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.protobuf.ByteString;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class AdaTextToSpeechClientTest {

    private static TextToSpeechClient mockCloudTTSClient = mock(TextToSpeechClient.class);
    private static AdaTextToSpeechClient clientUnderTest;
    private static ByteString validTestAudio;

    @Before
    public void setUp() throws IOException {
        validTestAudio =
                ByteString.copyFrom(
                        IOUtils.toByteArray(
                                new FileInputStream(
                                        FileSystems.getDefault()
                                                .getPath("src", "test", "java", "ada", "texttospeech", "valid_audio.wav")
                                                .toFile())));
        clientUnderTest = new AdaTextToSpeechClient(mockCloudTTSClient);
    }

    @Test
    public void getAudio_validAudio_Succeeds() throws Exception {
        when(mockCloudTTSClient.synthesizeSpeech(any(), any(), any()))
                .thenReturn(SynthesizeSpeechResponse.newBuilder().setAudioContent(validTestAudio).build());

        Optional<AudioInputStream> response = clientUnderTest.getAudio("Blah blah blah");
        AudioInputStream expectedInputStream =
                AudioSystem.getAudioInputStream(new ByteArrayInputStream(validTestAudio.toByteArray()));

        assertThat(response).isPresent();
        //noinspection OptionalGetWithoutIsPresent
        Assert.assertTrue(IOUtils.contentEquals(response.get(), expectedInputStream));
    }

    @Test
    public void getAudio_invalidAudio_isEmpty() {
        when(mockCloudTTSClient.synthesizeSpeech(any(), any(), any()))
                .thenReturn(
                        SynthesizeSpeechResponse.newBuilder().setAudioContent(ByteString.EMPTY).build());

        Optional<AudioInputStream> response = clientUnderTest.getAudio("Blah blah blah");

        assertThat(response).isEmpty();
    }

    @Test
    public void getAudio_nullInput_isEmpty() {
        when(mockCloudTTSClient.synthesizeSpeech(any(), any(), any())).thenReturn(null);

        Optional<AudioInputStream> response = clientUnderTest.getAudio(null);

        assertThat(response).isEmpty();
    }

//    @Test
//    @SuppressWarnings("InvalidArgument")
//    public void getAudio_invalidArgument_isEmpty() {
//        when(mockCloudTTSClient.synthesizeSpeech(any(), any(), any()))
//                .thenThrow(InvalidArgumentException.class);
//
//        Optional<AudioInputStream> response = clientUnderTest.getAudio("Whatever");
//
//        assertThat(response).isEmpty();
//    }
}

package ada.texttospeech;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

/**
 * An audio object with Linear16 encoding.
 */
class Linear16Audio {

    private final byte[] audio;

    Linear16Audio(byte[] audio) {
        this.audio = audio;
    }

    /**
     * Play the audio.
     */
    void play() {
        try {
            File tempFile = File.createTempFile("output", ".wav");
            CountDownLatch latch = new CountDownLatch(1);

            try (OutputStream out = new FileOutputStream(tempFile)) {
                out.write(audio);
                out.flush();
                Clip clip = AudioSystem.getClip();
                clip.addLineListener(
                        e -> {
                            if (e.getType() == LineEvent.Type.STOP) {
                                latch.countDown();
                            }
                        });
                clip.open(AudioSystem.getAudioInputStream(tempFile));
                clip.start();
            }
            latch.await();
        } catch (UnsupportedAudioFileException
                | LineUnavailableException
                | IOException
                | InterruptedException ex) {
            System.out.println(ex);
        }
    }
}

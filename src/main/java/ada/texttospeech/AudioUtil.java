package ada.texttospeech;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public final class AudioUtil {

    public static void play(AudioInputStream audio) {
        CountDownLatch syncLatch = new CountDownLatch(1);
        try {
            Clip clip = AudioSystem.getClip();

            // Listener which allow method return once sound is completed
            clip.addLineListener(
                    e -> {
                        if (e.getType() == LineEvent.Type.STOP) {
                            syncLatch.countDown();
                        }
                    });

            clip.open(audio);
            clip.start();
            syncLatch.await();

        } catch (LineUnavailableException
                | IOException
                | NullPointerException
                | InterruptedException e) {
            System.out.println(e);
        }
    }
}

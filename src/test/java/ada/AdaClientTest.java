package ada;

import ada.gui.Ada;
import ada.postgresql.AdaDB;
import ada.testlib.FakeNetworkReader;
import ada.testlib.FakeNetworkSender;
import ada.texttospeech.AdaTextToSpeechClient;
import ada.texttospeech.AudioUtil;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Optional;
import java.util.Scanner;

/**
 * {@code ada.AdaClient} reads messages from and sends Messages to
 * {@link AdaServer}.
 */
@SuppressWarnings("WeakerAccess")
public class AdaClientTest {

    AdaDB testDatabase =
            new AdaDB("localhost",
                    "test");

    AdaClient testClient;

    @Before
    public void setUp() throws IOException {
        testDatabase.initPostgres();

        AdaServer.main(new String[0]);

        testClient = new AdaClient(testDatabase,
                null,
                new NetworkSocketClient(
                        "localhost",
                        6259),
                new FileInputStream(FileSystems.getDefault()
                        .getPath("src",
                                "test",
                                "java",
                                "ada",
                                "testfiles",
                                "valid_path.txt")
                        .toFile()));
    }

    @Test
    public void getMessage_sendsMessage() {
        testClient.run();
    }

    @After
    public void tearDown() {
        testDatabase.clear();
    }
}

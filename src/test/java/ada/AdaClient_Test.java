package ada;

import ada.postgresql.AdaDB;
import com.google.rpc.Code;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.sql.SQLException;
import java.util.Optional;

public class AdaClient_Test {

    private AdaDB testdb;
    private AdaServer host;
    private NetworkReader listenerReader;
    private NetworkSender listenerSender;
    Thread listenerThread;
    Thread hostThread;

    @Before
    public void setUp() throws SQLException {

        testdb = new AdaDB("localhost",
                "test");
        testdb.clear();
        testdb.initPostgres();
        host = new AdaServer(6259,
                testdb);
    }

    @Test
    public void testAdaClient_sendsValidUserInputSuccessfully() throws FileNotFoundException {
        FileInputStream userInput = new FileInputStream(FileSystems.getDefault()
                .getPath("src",
                        "test",
                        "java",
                        "ada",
                        "user_input1.txt")
                .toFile());

        hostThread =
                new Thread(
                        () -> {
                            //noinspection StatementWithEmptyBody
                            while (host.Tick()) {
                            }
                            host.close();
                        });
        hostThread.start();

        NetworkSocketClient socketClient = new NetworkSocketClient(
                "localhost",
                6259);

        listenerReader = new NetworkReader(socketClient);
        listenerSender = new NetworkSender(socketClient);

        listenerSender.SendMessage(UsernameRequest.create("Listener",
                false)
                .serialize());
        Optional<String> userResponse;
        do {
            userResponse = listenerReader.ReadMessage();
        } while (!userResponse.isPresent());

        Assert.assertEquals(userResponse.get(),
                UsernameResponse.create(true,
                        Code.OK)
                        .serialize());

        listenerThread =
                new Thread(
                        () -> {
                            Optional<String> message = Optional.empty();
                            while (!message.isPresent()) {
                                message = listenerReader.ReadMessage();
                            }
                            Assert.assertEquals(new JSONObject(message.get()).get(
                                    "msg"),
                                    "hi");
                            listenerReader.close();
                        });

        AdaClient testClient = new AdaClient(null,
                new NetworkSocketClient(
                        "localhost",
                        6259),
                userInput);

        listenerThread.start();

        testClient.run();

        listenerThread.interrupt();
        hostThread.interrupt();
    }

    @After
    public void tearDown() {
        listenerThread.interrupt();
        hostThread.interrupt();
        testdb.clear();
    }
}

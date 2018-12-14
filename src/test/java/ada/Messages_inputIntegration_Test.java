package ada;

import ada.postgresql.AdaDB;
import com.google.rpc.Code;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Tests of message inputs.
 */
@RunWith(JUnit4.class)
public class Messages_inputIntegration_Test {

    private static final int PORT = 6259;

    private AdaServer host;
    private AdaDB testDb;

    // There are two users involved in this test. They could be user a and
    // user b, but I decided to call them Guinevere and Lancelot because King
    // Arthur is awesome.
    private static final String USER_GUINEVERE = "Guinevere";
    private static final String USER_LANCELOT = "Lancelot";
    private static final String MESSAGE = "Hi, Lance";
    private NetworkSender sender_guin;
    private NetworkReader reader_guin;
    private NetworkSender sender_lance;
    private NetworkReader reader_lance;

    @Before
    public void setUp() throws SQLException {
        testDb = new AdaDB("localhost",
                "test");
        testDb.initPostgres();

        host = new AdaServer(PORT,
                testDb);

        NetworkSocketClient client1 = new NetworkSocketClient("localhost",
                PORT);
        sender_guin = new NetworkSender(client1);
        reader_guin = new NetworkReader(client1);

        NetworkSocketClient client2 = new NetworkSocketClient("localhost",
                PORT);
        sender_lance = new NetworkSender(client2);
        reader_lance = new NetworkReader(client2);

    }

    @Test
    public void host_forwardsValidMessages() throws SQLException {
        // Start the Server.
        Thread hostThread =
                new Thread(
                        () -> {
                            //noinspection StatementWithEmptyBody
                            while (host.Tick()) {
                            }
                            host.close();
                        });
        hostThread.start();

        // Set up Guin's user account.
        Optional<String> response;
        sender_guin.SendMessage(UsernameRequest.create(USER_GUINEVERE,
                false)
                .serialize());
        do {
            response = reader_guin.ReadMessage();
        } while (!response.isPresent());

        Assert.assertEquals(response.get(),
                UsernameResponse.create(true,
                        Code.OK)
                        .serialize());

        // Set up Lance's user account.
        sender_lance.SendMessage(UsernameRequest.create(USER_LANCELOT,
                false)
                .serialize());
        do {
            response = reader_lance.ReadMessage();
        } while (!response.isPresent());

        Assert.assertEquals(response.get(),
                UsernameResponse.create(true,
                        Code.OK)
                        .serialize());

        // Great! Now that the accounts are set up, Guin can send Lance a
        // message.
        Optional<String> testMessage;
        sender_guin.SendMessage(MESSAGE);
        do {
            testMessage = reader_lance.ReadMessage();
        } while (!testMessage.isPresent());
        hostThread.interrupt();

        // Check that the correct message was sent.
        Assert.assertEquals(USER_GUINEVERE,
                new JSONObject(testMessage.get()).get("sender"));
        Assert.assertEquals(MESSAGE,
                new JSONObject(testMessage.get()).get("msg"));

        // Now check that it was stored in the database.
        String databaseEntry = testDb.Query(USER_LANCELOT);
        // This might be a little flaky-- maybe see if we can pack it
        // into a json or something.
        Assert.assertEquals(USER_GUINEVERE + ": " + MESSAGE + "\n",
                databaseEntry);
    }

    @After
    public void tearDown() {
        testDb.clear();
    }

}

package ada.postgresql;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.sql.*;

/**
 * Tests for AdaDB's message handling. In a separate file because generating
 * a long message takes a while.
 */
@RunWith(JUnit4.class)
public class AdaDbMessagesTest {

    private static AdaDB TEST_DB;

    @Before
    public void setUp() {
        TEST_DB = new AdaDB("localhost", "test");
        TEST_DB.initPostgres();
    }

    @Test
    public void test_validMessage_succeeds() throws SQLException {
        insertMessageForUsers("sparkles", "stars", "a totally valid message " +
                "except for flagrant abuse of capitalization.");
    }

    @Test
    public void test_emptyMessage_succeeds() throws SQLException {
        insertMessageForUsers("sparkles", "stars", "");
    }

    @Test
    public void test_insertLongMessage_succeeds() throws SQLException {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < 3_000_000; i++) {
            message.append('0');
        }
        insertMessageForUsers("unicorns", "rainbows", message.toString());
    }

    private void insertMessageForUsers(String sender, String receiver,
                                       String message) throws SQLException {
        Assert.assertTrue(TEST_DB.createUser(receiver));
        Assert.assertTrue(TEST_DB.createUser(sender));

        TEST_DB.insert(new JSONObject().put("sender", sender).put("msg",
                message), receiver);

        Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/", "postgres", "postgres");
        Statement stmt = connection.createStatement();

        ResultSet resultSet = stmt.executeQuery("select message from " +
                "testchattable where sender='" + sender + "'");
        Assert.assertTrue(resultSet.next());
        Assert.assertEquals(message, resultSet.getString(1));
    }

    @After
    public void tearDown() {
        TEST_DB.clear();
    }
}

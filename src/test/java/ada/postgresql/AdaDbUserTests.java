package ada.postgresql;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.sql.SQLException;

/**
 * Tests for the AdaDB user logic.
 */
@RunWith(JUnit4.class)
public class AdaDbUserTests {

    private static final String DATABASE_USERNAME = "postgres";
    private static final String DATABASE_PASSWORD = "postgres";
    private static final String TEST_HOST = "jdbc:postgresql://localhost:5432";
    private static AdaDB TEST_DB;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // TODO: put config in flags.

    @Before
    public void setUp() throws SQLException {
        TEST_DB = new AdaDB("localhost", "test");
        TEST_DB.initPostgres();
    }

    /**
     * test user names (manually remove entries later)
     */
    @Test
    public void test_checkUser_checksForNonexistentUser() throws SQLException {
        Assert.assertFalse(TEST_DB.containsUser("Max"));
    }


    @Test
    public void test_checkUser_checksForExistingUser() throws SQLException {
        Assert.assertFalse(TEST_DB.containsUser("Castille"));
        TEST_DB.createUser("Castille");
        Assert.assertTrue(TEST_DB.containsUser("Castille"));
    }

    @Test
    public void createUser_validUsername_succeeds() throws SQLException {
        Assert.assertFalse(TEST_DB.containsUser("credence"));
        Assert.assertTrue(TEST_DB.createUser("credence"));
    }

    @Test
    public void createUser_crazySqlCharacters_succeeds() throws SQLException {
        String crazyUsername = "totally'legal";
        Assert.assertFalse(TEST_DB.containsUser(crazyUsername));
        Assert.assertTrue(TEST_DB.createUser(crazyUsername));
    }

    @Test
    public void createUser_existing_returnsFalse() throws SQLException {
        Assert.assertTrue(TEST_DB.createUser("frerin"));
        Assert.assertFalse(TEST_DB.createUser("frerin"));
    }

    @Test
    public void insertValidMessage_succeeds() throws SQLException {
        String sender = "Astolat";
        String receiver = "Lancelot";
        String msg = "I prefer oceans, actually.";
        TEST_DB.createUser(sender);
        TEST_DB.createUser(receiver);

        TEST_DB.insert(new JSONObject().put("sender",
                sender)
                        .put("msg",
                                msg),
                receiver);
        String history = TEST_DB.Query(receiver);
        Assert.assertTrue(history.contains(sender));
        Assert.assertTrue(history.contains(msg));
    }


    /**
     * insert illegal message
     */
    @Test
    public void insertMessage_validSql_succeedsAndDoesNotDestroyDatabase() throws SQLException {
        String sender = "Lina";
        String receiver = "Clemency";
        String msg = "DROP table adaUser CASCADE;";

        TEST_DB.createUser(sender);
        TEST_DB.createUser(receiver);

        JSONObject jobj = new JSONObject();
        jobj.put("sender", sender);
        jobj.put("msg",
                msg);

        TEST_DB.insert(jobj, receiver);

        String history = TEST_DB.Query(receiver);
        Assert.assertTrue(history.contains(sender));
        Assert.assertTrue(history.contains(msg));
    }

    @Test
    public void insertMessage_noSender_fails() throws SQLException {
        expectedException.expect(JSONException.class);
        String test_user = "Max";
        TEST_DB.createUser(test_user);

        JSONObject jobj = new JSONObject();
        jobj.put("msg", "DROP table adaUser CASCADE");

        TEST_DB.insert(jobj, test_user);
    }

    @After
    public void tearDown() {
        TEST_DB.clear();
    }
}

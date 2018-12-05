package ada.postgresql;

import org.json.JSONObject;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AdaDbTest {

    private static final String DATABASE_USERNAME = "postgres";
    private static final String DATABASE_PASSWORD = "postgres";
    private static final String TEST_HOST = "jdbc:postgresql://localhost:5432";
    private AdaDB TEST_DB;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    // TODO: remove configuration tests and put config in flags.

    @Before
    public void setUp() throws Exception {
        TEST_DB = new AdaDB("localhost", "test");
        TEST_DB.initPostgres();
    }

    //    @Test
    //    public void testInitPostgres() {
    //        TEST_DB = new AdaDB("localhost", "test");
    //        TEST_DB.initPostgres();
    //    }

    /**
     * tests setup on your computer.
     */
    //    @Test(expected = PSQLException.class)
    //    public void getConnection_wrongPort_throwsPSQLException() throws Exception {
    //        Class.forName("org.postgresql.Driver");
    //        getConnection("jdbc:postgresql://localhost:5555", DATABASE_USERNAME, DATABASE_PASSWORD);
    //    }
    //
    //    @Test(expected = PSQLException.class)
    //    public void test_wrongUser_throwsPasswordException() throws Exception {
    //        Class.forName("org.postgresql.Driver");
    //        getConnection(TEST_HOST, ".", DATABASE_PASSWORD);
    //    }
    //
    //    @Test
    //    public void test_validUser_Succeeds() throws Exception {
    //        Class.forName("org.postgresql.Driver");
    //        getConnection(TEST_HOST, DATABASE_USERNAME, DATABASE_PASSWORD);
    //    }
    //
    //    //    /** test user names (manually remove entries later)
    //    //     */
    @Test
    public void createUser_validUsername_succeeds() {
        Assert.assertFalse(TEST_DB.checkUser("credence"));
        Assert.assertTrue(TEST_DB.createUser("credence"));
    }

    //
    //
    @Test
    public void createUser_crazySqlCharacters_succeeds() {
        String crazyUsername = "totally'legal";
        Assert.assertFalse(TEST_DB.checkUser(crazyUsername));
        Assert.assertTrue(TEST_DB.createUser(crazyUsername));
    }

    //
    @Test
    public void test_createExistingUser_returnsFalse() {
        Assert.assertTrue(TEST_DB.createUser("frerin"));
        Assert.assertFalse(TEST_DB.createUser("frerin"));
    }

    //
    //    /**
    //     * insert illegal message
    //     */
    @Test
    public void insertMessage_validSql_succeedsAndDoesNotDestroyDatabase() {
        String sender = "Lina";
        String receiver = "Clemency";

        TEST_DB.createUser(sender);
        TEST_DB.createUser(receiver);

        JSONObject jobj = new JSONObject();
        jobj.put("sender", sender);
        jobj.put("msg", "DROP table adaUser CASCADE;");

        TEST_DB.insert(jobj, receiver);

        Assert.assertTrue(TEST_DB.checkUser(sender));
    }

    //
    //    @Test
    //    public void insertMessage_noSender_fails() {
    //        expectedException.expect(JSONException.class);
    //        TEST_DB.createUser(TEST_USER);
    //
    //        JSONObject jobj = new JSONObject();
    //        jobj.put("msg", "DROP table adaUser CASCADE");
    //
    //        TEST_DB.insert(jobj, TEST_USER);
    //    }
    //
    @After
    public void tearDown() throws Exception {
        TEST_DB.clear();
        TEST_DB.close();
    }
}

package ada.postgresql;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.postgresql.util.PSQLException;

import static java.sql.DriverManager.getConnection;

@RunWith(JUnit4.class)
public class DB_Test {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * test setup
     */
    @Test(expected = PSQLException.class)
    public void getConnection_wrongPort_throwsPSQLException() throws Exception {
        Class.forName("org.postgresql.Driver");
        getConnection("jdbc:postgresql://localhost:5555", "postgres", "postgres");
    }

    @Test
    public void test_wrongUser_throwsPasswordException() throws Exception {
        expectedException.expect(PSQLException.class);
        expectedException.expectMessage("password authentication failed");
        Class.forName("org.postgresql.Driver");
        getConnection("jdbc:postgresql://localhost:5432", ".", "postgres");
    }

    //    /** test user names (manually remove entries later)
    //     *
    //     */
    //    @Test
    //    public void testIllegalUsername() {
    //        String username;
    //        String flag;
    //
    //        username = "somthing'illegal'";
    //        String random = Double.toString(Math.random());
    //        username = username + random;
    //        flag = "n";
    //        new UserUtil();
    //        Boolean ret = UserUtil.main(username, flag);
    //        Assert.assertEquals(true, ret);
    //
    //    }
    //
    //    @Test
    //    public void testDoubleInsert() {
    //        String username;
    //        String flag;
    //
    //        username = "somthing'illegal'";
    //        String random = Double.toString(Math.random());
    //        username = username + random;
    //        flag = "n";
    //        new UserUtil();
    //        Boolean ret = UserUtil.main(username, flag);
    //        Assert.assertEquals(true, ret);
    //        ret = UserUtil.main(username, flag);
    //        Assert.assertEquals(false, ret);
    //    }

    //    @Test
    //    public void testYesMatchName() {
    //        String username;
    //        String flag;
    //
    //        username = "somthing'illegal'";
    //        flag = "y";
    //        new UserUtil();
    //        Boolean ret = UserUtil.checkUser(username, flag);
    //        Assert.assertEquals(true, ret);
    //    }

    //    @Test
    //    public void testNoMatchName() {
    //        String username;
    //        String flag;
    //
    //        String random = Double.toString(Math.random());
    //        username = random;
    //        flag = "y";
    //        new UserUtil();
    //        Boolean ret = UserUtil.checkUser(username, flag);
    //        Assert.assertEquals(false, ret);
    //    }

    //    /** insert illegal message
    //     *
    //     */
    //    @Test
    //    public void noUsername() throws Exception {
    //        String username;
    //        String receiver;
    //        String flag;
    //
    //        username = "one";
    //        String random = Double.toString(Math.random());
    //        username = username + random;
    //        flag = "n";
    //        new UserUtil();
    //        Boolean ret = UserUtil.main(username, flag);
    //        Assert.assertEquals(true, ret);
    //
    //        receiver = username;
    //
    //        JSONObject jobj = new JSONObject();
    //        jobj.put("sender", "somthing'illegal'");
    //        jobj.put("msg", "DROP table adaUser CASCADE");
    //
    //        new ChatUtil();
    //        ChatUtil.main(jobj, receiver);
    //
    //    }

}

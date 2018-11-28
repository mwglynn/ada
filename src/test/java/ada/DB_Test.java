package ada;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.sql.Connection;

import static java.sql.DriverManager.getConnection;

@RunWith(JUnit4.class)
public class DB_Test {

    /** test setup
     *
     */
    @Test
    public void testWrongPort() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = getConnection("jdbc:postgresql://localhost:5555",
                    "postgres", "postgres");

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Test
    public void testWrongUser() {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = getConnection("jdbc:postgresql://localhost:5432",
                    ".", "postgres");

        } catch (Exception e) {
            System.err.println(e);
        }
    }

//    /** test user names (manually remove entries later)
//     *
//     */
//    @Test
//    public void testIllegalUsername() {
//        String username = null;
//        String flag = null;
//
//        username = "somthing'illegal'";
//        String random = Double.toString(Math.random());
//        username = username + random;
//        flag = "n";
//        new PostgreSQL_createUser();
//        Boolean ret = PostgreSQL_createUser.main(username, flag);
//        Assert.assertEquals(true, ret);
//
//    }
//
//    @Test
//    public void testDoubleInsert() {
//        String username = null;
//        String flag = null;
//
//        username = "somthing'illegal'";
//        String random = Double.toString(Math.random());
//        username = username + random;
//        flag = "n";
//        new PostgreSQL_createUser();
//        Boolean ret = PostgreSQL_createUser.main(username, flag);
//        Assert.assertEquals(true, ret);
//        ret = PostgreSQL_createUser.main(username, flag);
//        Assert.assertEquals(false, ret);
//    }

//    @Test
//    public void testYesMatchName() {
//        String username = null;
//        String flag = null;
//
//        username = "somthing'illegal'";
//        flag = "y";
//        new PostgreSQL_createUser();
//        Boolean ret = PostgreSQL_createUser.checkUser(username, flag);
//        Assert.assertEquals(true, ret);
//    }

//    @Test
//    public void testNoMatchName() {
//        String username = null;
//        String flag = null;
//
//        String random = Double.toString(Math.random());
//        username = random;
//        flag = "y";
//        new PostgreSQL_createUser();
//        Boolean ret = PostgreSQL_createUser.checkUser(username, flag);
//        Assert.assertEquals(false, ret);
//    }

//    /** insert illegal message
//     *
//     */
//    @Test
//    public void noUsername() throws Exception {
//        String username = null;
//        String receiver = null;
//        String flag = null;
//
//        username = "one";
//        String random = Double.toString(Math.random());
//        username = username + random;
//        flag = "n";
//        new PostgreSQL_createUser();
//        Boolean ret = PostgreSQL_createUser.main(username, flag);
//        Assert.assertEquals(true, ret);
//
//        receiver = username;
//
//        JSONObject jobj = new JSONObject();
//        jobj.put("sender", "somthing'illegal'");
//        jobj.put("msg", "DROP table adaUser CASCADE");
//
//        new PostgreSQL_insertChat();
//        PostgreSQL_insertChat.main(jobj, receiver);
//
//    }






}

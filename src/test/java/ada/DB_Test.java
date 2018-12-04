package ada;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.sql.*;

import static java.sql.DriverManager.getConnection;

@RunWith(JUnit4.class)
public class DB_Test {

    /** test setup
     *
     */
    @Test
    public void testWrongPort() throws Exception {
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
    public void testWrongUser() throws Exception {
        Connection c = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = getConnection("jdbc:postgresql://localhost:5432",
                    "postgres", "postgres");

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /** test user names (manually remove entries later)
     *
     */
    @Test
    public void login_newUser() {
        /**
         * this test tries to create a username with a special character
         * the test should pass because we do not yet have restrictions
         * on characters that may be used for usernames
         *
         * The test then checks a double insertion (trying to create a
         * username that already exists) and should fail
         */
        String userName = null;
        String host = "localhost";
        String flag = null;
        String random = Double.toString(Math.random());

        userName = "l'sa";
        userName = userName + random;
        flag = "n";
        PostgreSQLJDBC.InitPostgres("localhost");
        new PostgreSQL_createUser();
        Boolean ret = PostgreSQL_createUser.Create(host, userName, flag);
        Assert.assertEquals(true, ret);

        /* try to create the same username again */
        ret = PostgreSQL_createUser.Create(host, userName, flag);
        Assert.assertEquals(false, ret);

    }


    @Test
    public void login_existingUser() {
        /**
         * test looks as using an existing username.
         * the test first fails because the username does not exist
         * then passes on inserting the new username
         * then returns true for checking that the new username exists
         */
        String userName = null;
        String host = "localhost";
        String flag = null;
        String random = Double.toString(Math.random());

        userName = "katz";
        userName = userName + random;
        flag = "y";
        new PostgreSQL_createUser();
        Boolean ret = PostgreSQL_createUser.checkUser(host, userName, flag);
        /* should fail because the user does not exist yet */
        Assert.assertEquals(false, ret);

        flag = "n";
        new PostgreSQL_createUser();
        ret = PostgreSQL_createUser.Create(host, userName, flag);
        /* should pass now because the user is new */
        Assert.assertEquals(true, ret);

        flag = "y";
        new PostgreSQL_createUser();
        ret = PostgreSQL_createUser.checkUser(host, userName, flag);
        /* should pass now because the user is new */
        Assert.assertEquals(true, ret);
    }



    @Test
    public void message_longAndShortMessage() {
        /**
         * insert long message, still need to put better junit assert around it
         */
        String userName1 = null;
        String userName2 = null;
        String host = "localhost";
        String flag = null;
        String random = Double.toString(Math.random());

        userName1 = "rainbows";
        userName1 = userName1 + random;
        flag = "n";
        new PostgreSQL_createUser();
        Boolean ret = PostgreSQL_createUser.Create(host, userName1, flag);
        /* should pass now because the user is new */
        Assert.assertEquals(true, ret);

        userName2 = "butterflies";
        userName2 = userName2 + random;
        flag = "n";
        new PostgreSQL_createUser();
        ret = PostgreSQL_createUser.Create(host, userName2, flag);
        /* should pass now because the user is new */
        Assert.assertEquals(true, ret);

        String message = null;
        message = "start ";
        int count = 0;
        Boolean end = false;
        // send a really long message to test length
        while (end != true) {
            if (count == 10000) {
                end = true;
            } else {
                message = message + Double.toString(Math.random());
                count += 1;
            }
        }
        message = message + " end";
        System.out.println(message);
        String sender = userName1;
        String receiver = userName2;
        JSONObject jobj = new JSONObject();
        jobj.put("sender", sender);
        jobj.put("msg", message);

        String almost_empty = "";

        // long
        try {
            new PostgreSQL_insertChat();
            // long message
            PostgreSQL_insertChat.Insert(host, jobj, receiver);
            // short message
            jobj.put("msg", almost_empty);
            PostgreSQL_insertChat.Insert(host, jobj, receiver);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

}

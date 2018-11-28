package ada;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static java.sql.DriverManager.getConnection;

public class PostgreSQL_insertChat {
    public static void Insert(String host, JSONObject jobj, String receiver) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.postgresql.Driver");
            c = getConnection("jdbc:postgresql://" + host + ":5432",
                    "postgres", "postgres");
            c.setAutoCommit(false);

            String sender = jobj.getString("sender");
            String message = jobj.getString("msg");


            stmt = c.createStatement();
            String sql = "INSERT INTO adaChat " +
                    "(ID, date, time, message, sender, receiver) VALUES (DEFAULT, NOW()::date, NOW()::time," + "?" + "," + "?" + "," + "?" + ");";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, message);
            ps.setString(2, sender);
            ps.setString(3, receiver);

            ps.execute();
//            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }
}
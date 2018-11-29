package ada.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static java.sql.DriverManager.getConnection;

public class UserUtil {
    public static boolean createUser(String host, String args) {
        Connection c;
        Statement stmt;

        try {
            Class.forName("org.postgresql.Driver");
            c = getConnection("jdbc:postgresql://" + host + ":5432", "postgres", "postgres");
            c.setAutoCommit(false);

            stmt = c.createStatement();
            String sql = "INSERT INTO adaUser (ID, userName) VALUES (DEFAULT, " + "?" + ");";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, args);
            ps.execute();

            //            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();

            return true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println("user name taken!");
            return false;
        }
    }

    public static boolean checkUser(String host, String args) {
        Connection c;
        Statement stmt;
        String result = "";
        //    String safe_args;
        boolean toReturn = false;

        try {
            Class.forName("org.postgresql.Driver");
            c = getConnection("jdbc:postgresql://" + host + ":5432", "postgres", "postgres");
            c.setAutoCommit(false);

            //      safe_args = args.replace("'", "");

            stmt = c.createStatement();
            //            String sql = "select exists(select 1 from adaUser where username=" + "'" +
            // safe_args + "'" + ");";
            String sql = "select exists(select 1 from adaUser where username=" + "?" + ");";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, args);
            ResultSet resultSet = ps.executeQuery();
            //            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                result = resultSet.getString(1);
            }
            resultSet.close();
            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if (result.equals("t")) {
            toReturn = true;
        }

        return toReturn;
    }
}

package ada.postgresql;

import com.google.common.annotations.VisibleForTesting;
import org.json.JSONObject;

import java.io.Closeable;
import java.sql.*;

public class AdaDB implements Closeable {

    private static String HOST;
    private static String USER_TABLE;
    private static String CHAT_TABLE;

    public AdaDB(String host, String db_name) {
        USER_TABLE = db_name + "usertable";
        CHAT_TABLE = db_name + "chattable";
        HOST = host;
        // TODO: Make resistant to caps.
        try {
            /* for now, connect to default postgres */
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public AdaDB(String host) {
        new AdaDB(host, "ada");
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://" + HOST + ":5432/", "postgres", "postgres");
    }

    //  private Connection getTempConnection() throws SQLException {
    //    return getConnection();
    //  }

    public void initPostgres() {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            /* execute creation of user */
            String sql =
                    "CREATE TABLE IF NOT EXISTS "
                            + USER_TABLE
                            + " ("
                            + "ID SERIAL NOT NULL,"
                            + " userName VARCHAR(255) UNIQUE NOT NULL,"
                            + " PRIMARY KEY (ID, userName))";
            System.out.println(sql);
            stmt.executeUpdate(sql);
            connection.commit();
            System.out.println("- " + USER_TABLE + " created");

            /* execute creation of chat */
            sql =
                    "CREATE TABLE IF NOT EXISTS "
                            + CHAT_TABLE
                            + " ("
                            + "ID SERIAL NOT NULL,"
                            + "time TIME NOT NULL,"
                            + "date DATE NOT NULL,"
                            + "message TEXT,"
                            + "sender VARCHAR(255),"
                            + "receiver VARCHAR(255),"
                            + "PRIMARY KEY (ID),"
                            + "FOREIGN KEY (sender) REFERENCES "
                            + USER_TABLE
                            + "(userName) ON DELETE NO ACTION)";
            stmt.executeUpdate(sql);
            connection.commit();
            System.out.println("- " + CHAT_TABLE + " created");
            System.out.println(" SELECT current_database();");
            ResultSet s = stmt.executeQuery(" SELECT current_database();");
            while (s.next()) {
                System.out.println("Next");
                System.out.println(s.getString(1));
                ;
            }

            //        System.out.println("SELECT * FROM testuser");
            //        ResultSet r = stmt.executeQuery("\\dt;");
            //        "SELECT * FROM pg_catalog.pg_tables;
            //        while (r.next()) {
            //          System.out.println("NExt");
            //          System.out.println(r.getString(1));
            //        }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Tables created successfully");
    }

    public void Query(String username) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            System.out.println(username);

            String sql = "SELECT * FROM " + CHAT_TABLE;
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println(rs.getString(4));
                }
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(1);
        }
    }

    public void insert(JSONObject jobj, String receiver) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);

            String sender = jobj.getString("sender");
            String message = jobj.getString("msg");

            String sql =
                    "INSERT INTO "
                            + CHAT_TABLE
                            + "(ID, date, time, message, sender, receiver) VALUES (DEFAULT, NOW()::date, NOW()::time,"
                            + "?"
                            + ","
                            + "?"
                            + ","
                            + "?"
                            + ");";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, message);
            ps.setString(2, sender);
            ps.setString(3, receiver);

            ps.execute();

            connection.commit();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(1);
        }
    }

    public boolean createUser(String userName) {
        if (!checkUser(userName)) {
            try (Connection connection = getConnection();
                 Statement stmt = connection.createStatement()) {
                connection.setAutoCommit(false);
                String sql = "INSERT INTO " + USER_TABLE + " (ID, userName) VALUES (DEFAULT, " + "?" + ");";

                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, userName);
                System.out.println(ps.toString());
                ps.execute();

                connection.commit();
                return true;
            } catch (SQLException e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
        return false;
    }

    public boolean checkUser(String userName) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            String sql = "select id from " + USER_TABLE + " WHERE userName = '?';";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userName);

            //      ps.setString(2, userName);
            //      System.out.println(ps.toString());
            System.out.println(sql);
            ResultSet resultSet = stmt.executeQuery(sql);
            System.out.println(resultSet.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //
        //      while (resultSet.next()) {
        //        return resultSet.getString(1).equals("t");
        //      }
        //      resultSet.close();
        //      stmt.close();
        //      connection.commit();
        //    } catch (SQLException e) {
        //      e.printStackTrace();
        //    }
        return false;
    }

    @VisibleForTesting
    void clear() {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            // TODO: FIX
            stmt.execute("DROP TABLE " + CHAT_TABLE + ", " + USER_TABLE + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
    }
}

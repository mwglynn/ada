package ada.postgresql;

import com.google.common.annotations.VisibleForTesting;
import org.json.JSONObject;

import java.sql.*;

/**
 * The Ada Database class holds state for the database and handles queries.
 */
public class AdaDB {

    private final String HOST;
    private final String USER_TABLE;
    private final String CHAT_TABLE;

    public AdaDB(String host, String db_name) {
        // Translated to lower case because it seems to make a difference to
        // postgres.
        HOST = host;
        USER_TABLE = db_name.toLowerCase() + "usertable";
        CHAT_TABLE = db_name.toLowerCase() + "chattable";
        try {
            /* for now, connect to default postgres */
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://" + HOST + ":5432/", "postgres", "postgres");
    }

    public void initPostgres() {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            /* execute creation of user */
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS "
                            + USER_TABLE
                            + " ("
                            + "ID SERIAL NOT NULL,"
                            + " userName VARCHAR(255) UNIQUE NOT NULL,"
                            + " PRIMARY KEY (ID, userName))");
            connection.commit();

            /* execute creation of chat */
            stmt.executeUpdate(
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
                            + "(userName) ON DELETE NO ACTION)");
            connection.commit();
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
                            + "(ID, date, time, message, sender, receiver) " +
                            "VALUES (DEFAULT, NOW()::date, NOW()::time,"
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
            e.printStackTrace();
            System.exit(1);
        }
    }

    public boolean createUser(String userName) {
        if (!checkUser(userName)) {
            try (Connection connection = getConnection();
                 Statement stmt = connection.createStatement()) {
                connection.setAutoCommit(false);
                PreparedStatement ps =
                        connection.prepareStatement(
                                "INSERT INTO " + USER_TABLE + " (ID, " +
                                        "userName) VALUES (DEFAULT, ?)");
                ps.setString(1, userName);
                ps.execute();
                connection.commit();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean checkUser(String userName) {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            PreparedStatement ps =
                    connection.prepareStatement(
                            "select exists(select id from " + USER_TABLE + " " +
                                    "WHERE userName=?)");
            ps.setString(1, userName);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @VisibleForTesting
    void clear() {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE " + CHAT_TABLE + ", " + USER_TABLE + ";");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

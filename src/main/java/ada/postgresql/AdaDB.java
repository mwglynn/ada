package ada.postgresql;

import com.google.common.annotations.VisibleForTesting;
import org.json.JSONObject;

import java.io.Closeable;
import java.sql.*;

public class AdaDB implements Closeable {

    private static String HOST;
    private static String USER_TABLE;
    private static String CHAT_TABLE;

    public AdaDB(String host, String table_name) {
        HOST = host;
        USER_TABLE = table_name + "User";
        CHAT_TABLE = table_name + "Chat";
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
                "jdbc:postgresql://" + HOST + ":5432", "postgres", "postgres");
    }

    public void initPostgres() {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            System.out.println("Opened database successfully");

            /* execute creation of user */
            String sql =
                    "CREATE TABLE IF NOT EXISTS "
                            + USER_TABLE
                            + " ("
                            + "ID SERIAL NOT NULL,"
                            + "userName VARCHAR(255) UNIQUE NOT NULL,"
                            + "PRIMARY KEY (ID, userName))";
            stmt.executeUpdate(sql);
            System.out.println("- " + USER_TABLE + " executed");

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
            stmt.close();
            System.out.println("- " + CHAT_TABLE + " executed");

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
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
        String userExists = "";
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);
            String sql = "select exists(select 1 from " + USER_TABLE + " where username=" + "?" + ");";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userName);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                userExists = resultSet.getString(1);
            }
            resultSet.close();
            stmt.close();
            connection.commit();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return userExists.equals("t");
    }

    @VisibleForTesting
    void clear() {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            connection.setAutoCommit(false);

            String sql = "DROP TABLE IF EXISTS " + CHAT_TABLE + ", " + USER_TABLE + ";";
            System.out.println("Dropping Table!" + sql);

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.execute();

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
    }
}

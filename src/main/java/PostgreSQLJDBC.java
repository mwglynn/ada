import java.sql.Connection;
import java.sql.Statement;

import static java.sql.DriverManager.*;

/**
 * creation of the tables
 */
public class PostgreSQLJDBC {
    public static void main() {
        Connection c = null;
        Statement stmt = null;
        try {
            /* TODO: put connection in separate part == repeat code */

            /* for now, connect to default postgres */
            Class.forName("org.postgresql.Driver");
            c = getConnection("jdbc:postgresql://localhost:5432/public",
                            "postgres", "postgres");
            System.out.println("Opened database successfully");

            /* execute creation of user */
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS adaUser (" +
                    "ID SERIAL NOT NULL," +
                    "userName VARCHAR(255) UNIQUE NOT NULL," +
                    "PRIMARY KEY (ID, userName))";
            stmt.executeUpdate(sql);
            System.out.println("adaUser executed");

            /* execute creation of chat */
            sql = "CREATE TABLE IF NOT EXISTS adaChat (" +
                    "ID SERIAL NOT NULL," +
                    "time_stamp TIME NOT NULL," +
                    "message TEXT," +
                    "sender VARCHAR(255)," +
                    "receiver VARCHAR(255)[]," +
                    "PRIMARY KEY (ID)," +
                    "FOREIGN KEY (sender) REFERENCES adaUser(userName) ON DELETE NO ACTION)";
            stmt.executeUpdate(sql);
            stmt.close();
            System.out.println("adaChat executed");

            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Tables created successfully");
    }
}
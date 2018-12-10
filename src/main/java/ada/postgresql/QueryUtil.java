package ada.postgresql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class knows how to send a query to the database.
 */
class QueryUtil {

    static PreparedStatement GetHistory(Connection connection,
                                        String chatTable,
                                        String username) throws SQLException {
        PreparedStatement ps =
                connection.prepareStatement("SELECT * FROM " + chatTable
                        + " " + "WHERE sender=? OR " + "receiver=?");
        ps.setString(1,
                username);
        ps.setString(2,
                username);
        return ps;
    }
}

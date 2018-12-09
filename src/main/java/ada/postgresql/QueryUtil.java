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
        String sql = "SELECT * FROM " + chatTable + " WHERE sender=? OR " +
                "receiver=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1,
                username);
        ps.setString(2,
                username);
        return ps;
    }
}

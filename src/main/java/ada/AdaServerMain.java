package ada;

import ada.postgresql.AdaDB;

import java.sql.SQLException;

/**
 * ada.AdaServerMain houses the main for our Chat Server.
 */
@SuppressWarnings("WeakerAccess")
public class AdaServerMain {

    private static final int port = 6259;

    public static void main(String[] args) {
        try {
            AdaDB db = new AdaDB("localhost",
                    "ada");
            try (AdaServer host = new AdaServer(port,
                    db)) {
                /* DB: create tables if not exist */
                db.initPostgres();

                Thread hostThread =
                        new Thread(
                                () -> {
                                    while (host.Tick()) {
                                    }
                                    host.close();
                                });
                hostThread.start();

                hostThread.join();
            }
        } catch (InterruptedException | SQLException ie) {
            ie.printStackTrace();
        }
    }
}

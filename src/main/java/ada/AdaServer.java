package ada;

import ada.postgresql.AdaDB;

/**
 * ada.AdaServer houses the main for our Chat Server.
 */
@SuppressWarnings("WeakerAccess")
public class AdaServer {

    private static final int port = 6259;

    public static void main(String[] args) {
        AdaDB db = new AdaDB("localhost", "ada");
        TCPHost host = new TCPHost(port,
                db);
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

        try {
            hostThread.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

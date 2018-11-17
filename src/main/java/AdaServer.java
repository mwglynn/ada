/**
 * AdaServer houses the main for our Chat Server.
 */
@SuppressWarnings("WeakerAccess")
public class AdaServer {

    private static final int port = 6259;

    @SuppressWarnings("StatementWithEmptyBody")
    public static void main(String[] args) {
        TCPHost host = new TCPHost(port);

        /* setup tables */
        new PostgreSQLJDBC();
        PostgreSQLJDBC.main();

        Thread hostThread =
                new Thread(
                        () -> {
                            while (host.Tick()) {
                            }
                            host.Close();
                        });
        hostThread.start();

        try {
            hostThread.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

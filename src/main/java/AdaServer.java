/** AdaServer houses the main for our Chat Server. */
public class AdaServer {

  private static final int port = 6259;

  public static void main(String[] args) {
    TCPHost host = new TCPHost(port);

    Thread hostThread =
        new Thread(
            () -> {
              while (host.Tick()) ;
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

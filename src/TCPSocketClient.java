import java.io.IOException;
import java.net.*;

public class TCPSocketClient {
    private Socket clientSocket;
    public TCPSocketClient(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void writeToSocket(String msg) {

    }

    /* Closest thing to a destructor in Java */
    public void finalize() {
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

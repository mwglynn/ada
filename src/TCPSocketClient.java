import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class TCPSocketClient implements TCPSocket {
    private Socket clientSocket;
    private DataOutputStream clientOutputStream;
    public TCPSocketClient(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            clientOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void WriteToSocket(String msg) {
        try {
            clientOutputStream.writeBytes(msg);
            clientOutputStream.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /* Closest thing to a destructor in Java */
    public void Close() {
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

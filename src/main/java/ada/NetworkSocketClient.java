package ada;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;

public class NetworkSocketClient implements NetworkSocket {

    private Socket clientSocket;
    private DataOutputStream clientOutputStream;
    private BufferedReader clientInputStream;

    NetworkSocketClient(Socket socket) {
        try {
            clientSocket = socket;
            clientOutputStream =
                    new DataOutputStream(clientSocket.getOutputStream());
            clientInputStream =
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    NetworkSocketClient(String ip,
                        int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException();
        }
        try {
            clientSocket = new Socket(ip,
                    port);
            clientOutputStream =
                    new DataOutputStream(clientSocket.getOutputStream());
            clientInputStream =
                    new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            System.out.println("Please use 127.0.0.1!");
            /* e.printStackTrace(); */
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public void WriteToSocket(String msg) {
        try {
            clientOutputStream.writeBytes(msg + "\n");
            clientOutputStream.flush();
        } catch (IOException ioe) {
            //ioe.printStackTrace();
        }
    }

    @Override
    public Optional<String> ReadFromSocket() {
        try {
            if (clientInputStream.ready()) {
                return Optional.ofNullable(clientInputStream.readLine());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return Optional.empty();
    }

    /* Closest thing to a destructor in Java */
    @Override
    public void Close() {
        try {
            clientSocket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

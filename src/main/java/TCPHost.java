import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

class TCPHost {
  private ServerSocket serverSocket;
  private ArrayList<NetworkHandle> connectedSockets;
  private ConcurrentLinkedQueue<NetworkHandle> newConnections;
  private volatile boolean shouldListen;
  private Thread listenForConnectionsThread;

  private class NetworkHandle {
    NetworkSocket socket;
    NetworkReader reader;
    NetworkSender sender;
  }

  TCPHost(int port) {
    connectedSockets = new ArrayList<>();
    newConnections = new ConcurrentLinkedQueue<>();
    try {
      shouldListen = true;
      serverSocket = new ServerSocket(port);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    listenForConnectionsThread = new Thread(this::ListenForNewConnections);
    listenForConnectionsThread.start();
  }

  private void ListenForNewConnections() {
    while (shouldListen) {
      Socket newConnection = null;
      try {
        /* This line blocks until a client connects */
        newConnection = serverSocket.accept();

        System.out.println("Server got connection");
        NetworkHandle netHandle = new NetworkHandle();
        netHandle.socket = new NetworkSocketClient(newConnection);
        netHandle.reader = new NetworkReader(netHandle.socket);
        netHandle.sender = new NetworkSender(netHandle.socket);

        newConnections.add(netHandle);
      } catch (IOException ioe) {
        /* We will hit this whenever the server is shutdown */
      }
    }
  }

  boolean Tick() {
    while (!newConnections.isEmpty()) {
      connectedSockets.add(newConnections.poll());
    }

    for (int i = 0; i < connectedSockets.size(); i++) {
      Optional<String> msg;
      do {
        msg = connectedSockets.get(i).reader.ReadMessage();
        if (msg.isPresent()) {
          if (msg.get().equals("\\q")) {
            return false;
          }
          for (int j = 0; j < connectedSockets.size(); j++) {
            if (i != j) {
              connectedSockets.get(j).sender.SendMessage(msg.get());
            }
          }
        }
      } while (msg.isPresent());
    }

    return true;
  }

  void Close() {
    shouldListen = false;
    try {
      serverSocket.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    try {
      listenForConnectionsThread.join();
    } catch (InterruptedException ie) {
      ie.printStackTrace();
    }

    for (NetworkHandle connectedSocket : connectedSockets) {
      connectedSocket.sender.Close();
      connectedSocket.reader.Close();
      connectedSocket.socket.Close();
    }
  }
}

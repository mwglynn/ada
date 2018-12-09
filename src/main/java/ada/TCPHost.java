package ada;

import ada.postgresql.AdaDB;
import com.google.common.base.Preconditions;
import com.google.rpc.Code;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

class TCPHost implements Closeable {
    private ServerSocket serverSocket;
    private ArrayList<NetworkHandle> connectedSockets;
    private ConcurrentLinkedQueue<NetworkHandle> newConnections;
    private volatile boolean shouldListen;
    private Thread listenForConnectionsThread;
    private Hashtable<NetworkHandle, String> usernameMap;
    private AdaDB databaseManager;

    private class NetworkHandle {
        NetworkSocket socket;
        AdaNetworkReader reader;
        AdaNetworkSender sender;
    }

    TCPHost(int port,
            AdaDB adaDB) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException();
        }
        databaseManager = adaDB;
        usernameMap = new Hashtable<>();
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
            Socket newConnection;
            try {
                /* This line blocks until a client connects */
                newConnection = serverSocket.accept();

                System.out.println("Server got connection");
                NetworkHandle netHandle = new NetworkHandle();
                netHandle.socket = new NetworkSocketClient(newConnection);
                netHandle.reader = new AdaNetworkReader(netHandle.socket);
                netHandle.sender = new AdaNetworkSender(netHandle.socket);

                newConnections.add(netHandle);
            } catch (IOException ioe) {
                /* We will hit this whenever the server is shutdown */
            }
        }
    }


    boolean Tick() {
        // Deal with all new user requests coming in.
        Iterator<NetworkHandle> iterator = newConnections.iterator();
        while (iterator.hasNext()) {
            NetworkHandle newConnection = iterator.next();
            Optional<String> userRequest =
                    newConnection.reader.ReadMessage();
            if (userRequest.isPresent()) {
                System.out.println("Gettin my response");
                UsernameResponse response =
                        processNewUserRequest(userRequest.get(),
                                newConnection);

                newConnection.sender.SendMessage(response.serialize());
                if (response.usernameWasReceived()) {
                    connectedSockets.add(newConnection);
                    iterator.remove();
                }
            }
        }

        for (NetworkHandle connection : usernameMap.keySet()) {
            Optional<String> msg;
            msg = connection.reader.ReadMessage();
            if (msg.isPresent()) {
                if (msg.get()
                        .equals("\\q")) {
                    return false;
                } else if (msg.get()
                        .toLowerCase()
                        .equals(":history:")) {
                    JSONObject jobj = new JSONObject();
                    jobj.put("sender",
                            "query history");
                    jobj.put("msg",
                            databaseManager.Query(usernameMap.get(connection)));
                    connection.sender.SendMessage(jobj.toString());
                } else {
                    JSONObject jobj = new JSONObject();
                    jobj.put("sender",
                            usernameMap.get(connection));
                    jobj.put("msg",
                            msg.get());
                    String jsonMsg = jobj.toString();
                    for (NetworkHandle otherConnection : usernameMap.keySet()) {
                        if (otherConnection != connection) {
                            otherConnection.sender.SendMessage(jsonMsg);
                            databaseManager.insert(jobj,
                                    usernameMap.get(otherConnection));
                        }
                    }
                }
            }
        }
        return true;
    }

    private UsernameResponse processNewUserRequest(String message,
                                                   NetworkHandle handle) {
        UsernameRequest potentialUsername;
        System.out.println("Processinggg");
        try {
            potentialUsername = UsernameRequest.deserialize(message);
            Preconditions.checkArgument(potentialUsername.username() != null);
            Preconditions.checkArgument(!potentialUsername.username()
                    .isEmpty());
        } catch (Error e) {
            return UsernameResponse.create(false,
                    Code.INVALID_ARGUMENT);
        }

        System.out.println("Validdddddddddddd");
        if (usernameMap.contains(potentialUsername.username())) {
            return UsernameResponse.create(false,
                    Code.ALREADY_EXISTS);
        }
        System.out.println("Not already there");

        if (potentialUsername.isReturningUser()) {
            // If already connected or the database doesn't know this
            // username, return failed response.
            System.out.println("Is returning user!");
            if (!databaseManager.containsUser(potentialUsername.username())) {
                System.out.println("Database does not contain user!");
                return UsernameResponse.create(false,
                        Code.OK);
            } else {
                System.out.println("putting " + potentialUsername.username() + " in the usermap!");
                usernameMap.put(handle,
                        potentialUsername.username());
                return UsernameResponse.create(true,
                        Code.OK);
            }
        }
        // Request belongs to a new user.
        else {
            if (databaseManager.containsUser(potentialUsername.username())) {
                System.out.println("Already exists!");
                return UsernameResponse.create(false,
                        Code.ALREADY_EXISTS);
            } else {
                System.out.println("New user " + potentialUsername.username());
                usernameMap.put(handle,
                        potentialUsername.username());
                databaseManager.createUser(potentialUsername.username());
                return UsernameResponse.create(true,
                        Code.OK);
            }
        }
    }

    @Override
    public void close() {
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
            connectedSocket.sender.close();
            connectedSocket.reader.close();
            connectedSocket.socket.Close();
        }
    }
}

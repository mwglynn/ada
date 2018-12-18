package ada;

import ada.postgresql.AdaDB;
import com.google.common.base.Preconditions;
import com.google.rpc.Code;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

class AdaServer implements Closeable {
    private ServerSocket serverSocket;
    private ConcurrentLinkedQueue<NetworkHandle> newConnections;
    private volatile boolean shouldListen;
    private Thread listenForConnectionsThread;
    private Hashtable<NetworkHandle, String> connectedUsers;
    private AdaDB databaseManager;

    private class NetworkHandle {
        NetworkSocket socket;
        NetworkReader reader;
        NetworkSender sender;
    }

    AdaServer(int port,
              AdaDB adaDB) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException();
        }
        databaseManager = adaDB;
        connectedUsers = new Hashtable<>();
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
                netHandle.reader = new NetworkReader(netHandle.socket);
                netHandle.sender = new NetworkSender(netHandle.socket);

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
            if (newConnection == null || newConnection.socket == null || newConnection.sender == null || newConnection.reader == null) {
                iterator.remove();
                System.out.println("Error, null connection.");
                continue;
            }
            Optional<String> userRequest =
                    newConnection.reader.ReadMessage();
            if (userRequest.isPresent()) {
                System.out.println("Gettin my response");
                UsernameResponse response =
                        processNewUserRequest(userRequest.get(),
                                newConnection);

                newConnection.sender.SendMessage(response.serialize());
                if (response.usernameWasReceived()) {
                    iterator.remove();
                }
            }
        }

        for (NetworkHandle connection : connectedUsers.keySet()) {
            Optional<String> msg;
            msg = connection.reader.ReadMessage();
            if (msg.isPresent()) {
                if (msg.get()
                        .equals("SecretExitMessage")) {
                    return false;
                } else if (msg.get()
                        .toLowerCase()
                        .equals(":history:")) {
                    JSONObject jobj = new JSONObject();
                    jobj.put("sender",
                            "query history");
                    try {
                        jobj.put("msg",
                                databaseManager.Query(connectedUsers.get(connection)));
                    } catch (SQLException e) {
                        jobj.put("msg",
                                "Sorry, no history available!");
                    }
                    connection.sender.SendMessage(jobj.toString());
                } else {
                    JSONObject jobj = new JSONObject();
                    jobj.put("sender",
                            connectedUsers.get(connection));
                    jobj.put("msg",
                            msg.get());
                    String jsonMsg = jobj.toString();
                    for (NetworkHandle otherConnection :
                            connectedUsers.keySet()) {
                        if (otherConnection != connection) {
                            otherConnection.sender.SendMessage(jsonMsg);
                            try {
                                databaseManager.insert(jobj,
                                        connectedUsers.get(otherConnection));
                            } catch (SQLException e) {
                                // If insertion fails, log and move on.
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private UsernameResponse processNewUserRequest(String request,
                                                   NetworkHandle handle) throws IllegalArgumentException {
        UsernameRequest potentialUsername;
        System.out.println("Processinggg");
        try {
            potentialUsername = UsernameRequest.deserialize(request);
            Preconditions.checkArgument(potentialUsername.username() != null);
            Preconditions.checkArgument(!potentialUsername.username()
                    .isEmpty());
        } catch (Error e) {
            return UsernameResponse.create(false,
                    Code.INVALID_ARGUMENT);
        }

        System.out.println("Validdddddddddddd");
        if (connectedUsers.contains(potentialUsername.username())) {
            return UsernameResponse.create(false,
                    Code.ALREADY_EXISTS);
        }
        System.out.println("Not already there");
        try {
            if (potentialUsername.isReturningUser()) {
                // If already connected or the database doesn't know this
                // username, return failed response.
                System.out.println("Is returning user!");
                if (!databaseManager.containsUser(potentialUsername.username())) {
                    System.out.println("Database does not contain user!");
                    return UsernameResponse.create(false,
                            Code.INVALID_ARGUMENT);
                } else {
                    System.out.println("putting " + potentialUsername.username() + " in the usermap!");
                    connectedUsers.put(handle,
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
                    connectedUsers.put(handle,
                            potentialUsername.username());
                    if (!databaseManager.createUser(potentialUsername.username())) {
                        return UsernameResponse.create(false,
                                Code.INTERNAL);
                    }
                    return UsernameResponse.create(true,
                            Code.OK);
                }
            }
        } catch (SQLException e) {
            return UsernameResponse.create(false,
                    Code.INTERNAL);
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

        for (NetworkHandle connectedSocket : connectedUsers.keySet()) {
            connectedSocket.sender.close();
            connectedSocket.reader.close();
            connectedSocket.socket.close();
        }
    }
}

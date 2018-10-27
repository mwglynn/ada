import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.Thread;

public class NetworkReader {

    private ConcurrentLinkedQueue<String> incomingMessages;
    private volatile boolean shouldProcessReceiveQueue;
    private NetworkSocket clientSocket;
    private Thread receivingThread;


    NetworkReader(NetworkSocket socket) {
        incomingMessages = new ConcurrentLinkedQueue<>();
        shouldProcessReceiveQueue = true;
        clientSocket = socket;
        receivingThread = new Thread(this::ReadAvailable);
        receivingThread.start();
    }


    String ReadMessage() {
        if (shouldProcessReceiveQueue) {
            if (!incomingMessages.isEmpty()) {
                return incomingMessages.poll();
            }
        }
        return null;
    }


    private void ReadAvailable() {
        while (shouldProcessReceiveQueue) {
            String s = clientSocket.ReadFromSocket();
            if (s != null) {
                incomingMessages.add(s);
            }
        }
    }

    /* Closest thing to a destructor in Java */
    public void Close() {
        shouldProcessReceiveQueue = false;
        try {
            receivingThread.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.Thread;
import java.util.function.Function;

public class TCPMessageReader {

    private ConcurrentLinkedQueue<String> incomingMessages;
    private volatile boolean shouldProcessReceiveQueue;
    private TCPSocket clientSocket;
    private Thread receivingThread;


    TCPMessageReader(TCPSocket socket) {
        incomingMessages = new ConcurrentLinkedQueue<String>();
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

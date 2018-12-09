package ada;

import java.io.Closeable;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * main reader for incoming messages.
 */
public class AdaNetworkReader implements Closeable, NetworkReader {

    /**
     * An unbounded thread-safe queue based on linked nodes. This queue orders
     * elements FIFO
     * (first-in-first-out).
     */
    private final ConcurrentLinkedQueue<String> incomingMessages;

    private volatile boolean shouldProcessReceiveQueue;
    private final NetworkSocket clientSocket;
    private final Thread receivingThread;

    AdaNetworkReader(NetworkSocket socket) {
        incomingMessages = new ConcurrentLinkedQueue<>();
        shouldProcessReceiveQueue = true;
        clientSocket = socket;
        receivingThread = new Thread(this::ReadAvailable);
        receivingThread.start();
    }

    /**
     * Read incoming messages.
     */
    @Override
    public Optional<String> ReadMessage() {
        if (shouldProcessReceiveQueue && !incomingMessages.isEmpty()) {
            return Optional.ofNullable(incomingMessages.poll());
        }
        return Optional.empty();
    }

    /**
     * Incoming messages are streams.
     */
    private void ReadAvailable() {
        while (shouldProcessReceiveQueue) {
            clientSocket.ReadFromSocket().ifPresent(incomingMessages::add);
        }
    }

    /**
     * Closest thing to a destructor in Java.
     */
    @Override
    public void close() {
        shouldProcessReceiveQueue = false;
        try {
            receivingThread.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

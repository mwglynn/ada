package ada;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * main reader for incoming messages.
 */
class NetworkReader {

    /**
     * An unbounded thread-safe queue based on linked nodes. This queue orders
     * elements FIFO
     * (first-in-first-out).
     */
    private final ConcurrentLinkedQueue<String> incomingMessages;

    private volatile boolean shouldProcessReceiveQueue;
    private final NetworkSocket clientSocket;
    private final Thread receivingThread;

    NetworkReader(NetworkSocket socket) {
        incomingMessages = new ConcurrentLinkedQueue<>();
        shouldProcessReceiveQueue = true;
        clientSocket = socket;
        receivingThread = new Thread(this::ReadAvailable);
        receivingThread.start();
    }

    Optional<String> ReadMessage() {
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
    void Close() {
        shouldProcessReceiveQueue = false;
        try {
            receivingThread.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

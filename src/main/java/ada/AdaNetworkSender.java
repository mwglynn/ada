package ada;

import java.io.Closeable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * main sending functionality.
 */
class AdaNetworkSender implements Closeable, NetworkSender {

    /**
     * An unbounded thread-safe queue based on linked nodes. This queue
     * orders elements FIFO
     * (first-in-first-out).
     */
    private final ConcurrentLinkedQueue<String> outgoingMessages;

    private volatile boolean shouldProcessSendQueue;
    private final NetworkSocket clientSocket;
    private final Thread sendingThread;

    AdaNetworkSender(NetworkSocket socket) {
        outgoingMessages = new ConcurrentLinkedQueue<>();
        shouldProcessSendQueue = true;
        clientSocket = socket;
        sendingThread = new Thread(this::SendAvailable);
        sendingThread.start();
    }

    /**
     * Sends a message.
     */
    @Override
    public void SendMessage(String msg) {
        if (shouldProcessSendQueue) {

            /* DB: check for history command */
            if (msg.equals(":history:")) {
                System.out.println("coming soon!");
                //                new HistoryUtil();
                //                HistoryUtil.main(msg);
            } else {
                /* we don't want to send
                 * our own request for history
                 */
                outgoingMessages.add(msg);
            }
        }
    }

    private void SendAvailable() {
        while (shouldProcessSendQueue || !outgoingMessages.isEmpty()) {
            if (!outgoingMessages.isEmpty()) {
                clientSocket.WriteToSocket(outgoingMessages.poll());
            }
        }
    }

    /**
     * Closest thing to a destructor in Java.
     */
    @Override
    public void close() {
        shouldProcessSendQueue = false;
        try {
            sendingThread.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

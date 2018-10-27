import java.util.concurrent.ConcurrentLinkedQueue;
import java.lang.Thread;

class TCPMessageSender {

    private ConcurrentLinkedQueue<String> outgoingMessages;
    private volatile boolean shouldProcessSendQueue;
    private TCPSocket clientSocket;
    private Thread sendingThread;


    TCPMessageSender(TCPSocket socket) {
        outgoingMessages = new ConcurrentLinkedQueue<String>();
        shouldProcessSendQueue = true;
        clientSocket = socket;
        sendingThread = new Thread(this::SendAvailable);
        sendingThread.start();
    }

    void SendMessage(String msg) {
        if (shouldProcessSendQueue) {
            outgoingMessages.add(msg);
        }
    }

    private void SendAvailable() {
        while (shouldProcessSendQueue || !outgoingMessages.isEmpty()) {
            if (!outgoingMessages.isEmpty()) {
                clientSocket.WriteToSocket(outgoingMessages.poll());
            }
        }
    }

    /* Closest thing to a destructor in Java */
    public void Close() {
        shouldProcessSendQueue = false;
        try {
            sendingThread.join();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

}

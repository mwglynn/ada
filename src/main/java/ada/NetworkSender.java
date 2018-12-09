package ada;

/**
 * Simple interface for a class to send messages.
 */
public interface NetworkSender {

    /** @param msg a message to send. */
    void SendMessage(String msg);

    void close();
}

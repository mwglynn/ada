package ada.testlib;

import ada.NetworkReader;

import java.util.Optional;

public class FakeNetworkReader implements NetworkReader {
    private boolean messageWasRead = false;

    @Override
    public Optional<String> ReadMessage() {
        messageWasRead = true;
        return Optional.empty();
    }

    public boolean messageWasRead() {
        return messageWasRead;
    }

    public void reset() {
        messageWasRead = false;
    }

    @Override
    public void close() {
        // No op
    }
}

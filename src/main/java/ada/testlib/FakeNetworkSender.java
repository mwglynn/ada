package ada.testlib;

import ada.NetworkSender;

public class FakeNetworkSender implements NetworkSender {

    private boolean messageWasSent = false;

    @Override
    public void SendMessage(String msg) {
        messageWasSent = true;
    }

    public boolean messageWasSent() {
        return messageWasSent;
    }

    public void reset() {
        messageWasSent = false;
    }

    @Override
    public void close() {
        // No op
    }
}

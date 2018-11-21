package ada;

import java.util.Optional;

interface NetworkSocket {
    void WriteToSocket(String msg);

    Optional<String> ReadFromSocket();

    void Close();
}

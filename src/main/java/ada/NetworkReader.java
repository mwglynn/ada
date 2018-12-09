package ada;

import java.util.Optional;

/** A simple interface for a network reader. */
public interface NetworkReader {

    /** Reads available messages. */
    Optional<String> ReadMessage();

    void close();
}

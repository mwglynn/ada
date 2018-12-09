package ada;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.auto.value.AutoValue;
import com.google.rpc.Code;

@AutoValue
abstract class UsernameResponse {

    static UsernameResponse create(boolean usernameWasValid,
                                   Code errorStatus) {
        return new AutoValue_UsernameResponse(usernameWasValid,
                errorStatus);
    }

    abstract boolean usernameWasRegistered();

    abstract Code errorStatus();

    public String serialize() {
        return Boolean.toString(usernameWasRegistered()) + " " + errorStatus().getNumber();
    }

    public static UsernameResponse deserialize(String response) throws InvalidArgumentException {
        String[] resp = response.split("\\s");
        return UsernameResponse.create(Boolean.valueOf(resp[0]),
                Code.forNumber(Integer.valueOf(resp[1])));
    }
}

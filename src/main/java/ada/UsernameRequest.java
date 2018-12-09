package ada;

import com.google.api.gax.rpc.InvalidArgumentException;
import com.google.auto.value.AutoValue;
import com.google.common.base.Preconditions;

@AutoValue
abstract class UsernameRequest {

    static UsernameRequest create(String username,
                                  boolean alreadyExists) throws InvalidArgumentException {
        Preconditions.checkArgument(!username.isEmpty());
        Preconditions.checkArgument(!username.contains(" "));
        return new AutoValue_UsernameRequest(username,
                alreadyExists);
    }

    abstract String username();

    abstract boolean isReturningUser();

    public String serialize() {
        return username() + " " + Boolean.toString(isReturningUser());
    }

    public static UsernameRequest deserialize(String response) throws InvalidArgumentException {
        String[] resp = response.split(" ");
        return UsernameRequest.create(resp[0],
                Boolean.valueOf(resp[1]));
    }
}

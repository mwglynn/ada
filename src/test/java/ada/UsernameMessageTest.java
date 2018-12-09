package ada;

import com.google.rpc.Code;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class UsernameMessageTest {


    @Test
    public void check_deserializeSerializeUsernameRequest_isIdempotent() {
        UsernameRequest testRequest = UsernameRequest.create("Gawain",
                true);
        Assert.assertEquals(testRequest,
                UsernameRequest.deserialize(testRequest.serialize()));
    }


    @Test
    public void check_deserializeSerializeUsernameRequestNewUser_isIdempotent() {
        UsernameRequest testRequest = UsernameRequest.create("Gawain",
                false);
        Assert.assertEquals(testRequest,
                UsernameRequest.deserialize(testRequest.serialize()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void check_createBadUsername_throwsError() {
        UsernameRequest.create("Launcelot " +
                        "Guinevere",
                false);
    }

    @Test
    public void check_deserializeSerializeUsernameResponse_isIdempotent() {
        UsernameResponse testRequest = UsernameResponse.create(true,
                Code.OK);
        Assert.assertEquals(testRequest,
                UsernameResponse.deserialize(testRequest.serialize()));
    }


    @Test
    public void check_deserializeSerializeUsernameResponseErrorResponse_isIdempotent() {
        UsernameResponse testRequest = UsernameResponse.create(false,
                Code.INVALID_ARGUMENT);
        Assert.assertEquals(testRequest,
                UsernameResponse.deserialize(testRequest.serialize()));
    }
}

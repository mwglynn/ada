

package ada;

import com.google.rpc.Code;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_UsernameResponse extends UsernameResponse {

  private final boolean usernameWasReceived;

  private final Code errorStatus;

  AutoValue_UsernameResponse(
      boolean usernameWasReceived,
      Code errorStatus) {
    this.usernameWasReceived = usernameWasReceived;
    if (errorStatus == null) {
      throw new NullPointerException("Null errorStatus");
    }
    this.errorStatus = errorStatus;
  }

  @Override
  boolean usernameWasReceived() {
    return usernameWasReceived;
  }

  @Override
  Code errorStatus() {
    return errorStatus;
  }

  @Override
  public String toString() {
    return "UsernameResponse{"
         + "usernameWasReceived=" + usernameWasReceived + ", "
         + "errorStatus=" + errorStatus
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UsernameResponse) {
      UsernameResponse that = (UsernameResponse) o;
      return (this.usernameWasReceived == that.usernameWasReceived())
           && (this.errorStatus.equals(that.errorStatus()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= usernameWasReceived ? 1231 : 1237;
    h$ *= 1000003;
    h$ ^= errorStatus.hashCode();
    return h$;
  }

}

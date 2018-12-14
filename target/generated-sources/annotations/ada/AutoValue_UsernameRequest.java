

package ada;

import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_UsernameRequest extends UsernameRequest {

  private final String username;

  private final boolean isReturningUser;

  AutoValue_UsernameRequest(
      String username,
      boolean isReturningUser) {
    if (username == null) {
      throw new NullPointerException("Null username");
    }
    this.username = username;
    this.isReturningUser = isReturningUser;
  }

  @Override
  String username() {
    return username;
  }

  @Override
  boolean isReturningUser() {
    return isReturningUser;
  }

  @Override
  public String toString() {
    return "UsernameRequest{"
         + "username=" + username + ", "
         + "isReturningUser=" + isReturningUser
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof UsernameRequest) {
      UsernameRequest that = (UsernameRequest) o;
      return (this.username.equals(that.username()))
           && (this.isReturningUser == that.isReturningUser());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h$ = 1;
    h$ *= 1000003;
    h$ ^= username.hashCode();
    h$ *= 1000003;
    h$ ^= isReturningUser ? 1231 : 1237;
    return h$;
  }

}

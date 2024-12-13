package sg.edu.ntu.split_and_share.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException() {
    super("User not found.");
  }
}

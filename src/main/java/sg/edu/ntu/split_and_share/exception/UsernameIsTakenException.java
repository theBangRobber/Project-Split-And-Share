package sg.edu.ntu.split_and_share.exception;

public class UsernameIsTakenException extends RuntimeException {
  public UsernameIsTakenException() {
    super("Username already exists");
  }
}

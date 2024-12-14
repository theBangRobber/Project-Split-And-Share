package sg.edu.ntu.split_and_share.exception;

public class PaidByMemberNotFoundException extends RuntimeException {
  public PaidByMemberNotFoundException() {
    super("The person who paid for the expense must be part of the shared group members.");
  }
}

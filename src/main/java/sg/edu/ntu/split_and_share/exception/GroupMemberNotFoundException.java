package sg.edu.ntu.split_and_share.exception;

public class GroupMemberNotFoundException extends RuntimeException {
  public GroupMemberNotFoundException() {
    super("Group member not found.");
  }
}

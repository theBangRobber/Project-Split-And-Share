package sg.edu.ntu.split_and_share.exception;

public class DuplicateGroupMemberException extends RuntimeException {
    public DuplicateGroupMemberException() {
      super("There are duplicate member within the group");
    }
  }
package sg.edu.ntu.split_and_share.exception;

public class DuplicateGroupMemberException extends RuntimeException {
    public DuplicateGroupMemberException() {
      super("No duplication of entries allowed, please try again");
    }
  }
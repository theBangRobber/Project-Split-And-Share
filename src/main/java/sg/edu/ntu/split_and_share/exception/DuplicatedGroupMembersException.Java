package sg.edu.ntu.split_and_share.exception;

public class DuplicatedGroupMembersException extends RuntimeException {
    public DuplicatedGroupMembersException() {
      super("No duplication of entries allowed, please try again");
    }
  }
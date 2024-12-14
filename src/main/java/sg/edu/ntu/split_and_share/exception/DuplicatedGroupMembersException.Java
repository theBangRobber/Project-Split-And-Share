package sg.edu.ntu.split_and_share.exception;

public class DuplicatedGroupMembersException extends RuntimeException {
    public DuplicatedGroupMembersException() {
      super("There are duplicate member within the group");
    }
  }
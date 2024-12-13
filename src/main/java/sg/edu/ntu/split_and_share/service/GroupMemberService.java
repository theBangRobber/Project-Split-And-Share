package sg.edu.ntu.split_and_share.service;

import java.util.List;

public interface GroupMemberService {

  List<String> addGroupMembers(List<String> groupMemberList, String username);

  void removeGroupMember(String memberName, String username);

  List<String> getAllGroupMembers(String username);
}
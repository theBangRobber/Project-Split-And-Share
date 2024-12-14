package sg.edu.ntu.split_and_share.service;

import java.util.Set;

public interface GroupMemberService {

  Set<String> addGroupMembers(Set<String> groupMemberList, String username);

  void removeGroupMember(String memberName, String username);

  Set<String> getAllGroupMembers(String username);
}
package sg.edu.ntu.split_and_share.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import sg.edu.ntu.split_and_share.service.GroupMemberService;

@RestController
@RequestMapping("/api/group-members")
public class GroupMemberController {

  private GroupMemberService groupMemberService;

  public GroupMemberController(GroupMemberService groupMemberService) {
    this.groupMemberService = groupMemberService;
  }

  // Add group member(s) to user's dashboard
  // http://localhost:8080/api/group-members/add/{username}
  @PostMapping("/add/{username}")
  public ResponseEntity<List<String>> addGroupMembers(@PathVariable String username,
      @Valid @RequestBody List<String> groupMemberList) {
    return new ResponseEntity<>(groupMemberService.addGroupMembers(groupMemberList, username), HttpStatus.CREATED);
  }

  // Remove group member(s) from user's dashboard
  // http://localhost:8080/api/group-members/remove/{username}/{memberName}
  @DeleteMapping("/remove/{username}/{memberName}")
  public ResponseEntity<HttpStatus> removeGroupMember(@PathVariable String username, @PathVariable String memberName) {
    groupMemberService.removeGroupMember(memberName, username);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  // Get all group members
  // http://localhost:8080/api/group-members/list/{username}
  @GetMapping("/list/{username}")
  public ResponseEntity<List<String>> getAllGroupMembers(@PathVariable String username) {
    return new ResponseEntity<>(groupMemberService.getAllGroupMembers(username), HttpStatus.OK);
  }

}
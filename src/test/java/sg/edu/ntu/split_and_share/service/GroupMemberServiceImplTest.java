package sg.edu.ntu.split_and_share.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.entity.GroupMember;
import sg.edu.ntu.split_and_share.exception.DashboardNotFoundException;
import sg.edu.ntu.split_and_share.exception.DuplicateGroupMemberException;
import sg.edu.ntu.split_and_share.exception.GroupMemberNotFoundException;
import sg.edu.ntu.split_and_share.exception.UserNotFoundException;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;
import sg.edu.ntu.split_and_share.repository.GroupMemberRepository;



@ExtendWith(MockitoExtension.class)
public class GroupMemberServiceImplTest {

@Mock
private GroupMemberRepository groupMemberRepository;

@Mock
private DashboardRepository dashboardRepository;

@InjectMocks
private GroupMemberServiceImpl groupMemberService;

@Test
void addGroupMembers_Successful() {
    //Arrange - what is needed to add group members are valid dashboard and  no duplication of username.
    String username = "Jane"; //user's username that you want to add into the group
    Set<String> groupMembersList = new HashSet<>(Arrays.asList("John", "Doe")); //arrange a list of group member to be added into dashboard
    Dashboard janeDashboard = new Dashboard();
    janeDashboard.setId(1L);
    janeDashboard.setName("Jane's Dashboard");
    janeDashboard.setGroupMembers(new ArrayList<>()); //create an empty list of group members in mockDashboard.

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.of(janeDashboard));
    when(groupMemberRepository.save(any(GroupMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

    //Execute the method addGroupMember(Set<String> groupMemberList, String username) where the username is used to find the dashboard to add the group members in
    Set<String> addGroupMember = groupMemberService.addGroupMembers(groupMembersList, username); //what this method do is to find a dashboard using the username => Jane's dashboard and go through the groupMemberlist and add them into Jane's Dashboard

    assertEquals(groupMembersList, addGroupMember);
    verify(groupMemberRepository, times(2)).save(any(GroupMember.class)); //called twice cause save was conducted for two group member
}

@Test
public void testAddGroupMembers_Unsuccessful_DashboardNotFound(){
    String username = "nonExistentUser"; 
    Set<String> groupMembers = new HashSet<>(Arrays.asList("Alice", "Bob"));

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.empty()); //simulate no dashboard under that username

    assertThrows(DashboardNotFoundException.class, () -> groupMemberService.addGroupMembers(groupMembers, username)); //will throw an exception when try to add group member into a nonexistence user with no dashboard
}

@Test
public void testAddGroupMembers_Unsuccessful_DuplicatedGroupMember(){
    String username = "Jane"; // second parameter of the method
    Set<String> groupMembersList = new HashSet<>(Collections.singletonList("John")); //the first parameters of addGroupMember. uses Collections.singletonList to create a immutable single element list that contain "Alice"
    Dashboard janeDashboard = new Dashboard();
    janeDashboard.setId(1L);
    janeDashboard.setName("Jane's Dashboard");

    GroupMember existingMember = new GroupMember();
    existingMember.setMemberName("John");
    janeDashboard.setGroupMembers(new ArrayList<>(Set.of(existingMember))); //this is to simulate there is an existing groupmember in jane's dashboard

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.of(janeDashboard));

    assertThrows(DuplicateGroupMemberException.class, () -> groupMemberService.addGroupMembers(groupMembersList, username));
}

@Test
public void testRemoveGroupMember_Successful(){
    String username = "Jane";
    String memberName = "Alice";
    Dashboard janeDashboard = new Dashboard();
    janeDashboard.setId(1L);
    janeDashboard.setName("Jane's Dashboard");

    GroupMember groupMember = new GroupMember(); //initialise new group member
    groupMember.setMemberName(memberName); //added "ALice" into this group
    groupMember.setDashboard(janeDashboard); //link "Alice" to Jane's Dashboard
    groupMember.setBalance(0.0); //no expenses to return nor receive

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.of(janeDashboard));
    when(groupMemberRepository.findByDashboard_IdAndMemberName(janeDashboard.getId(), memberName))
            .thenReturn(Optional.of(groupMember)); 

    groupMemberService.removeGroupMember(memberName, username);

    verify(groupMemberRepository, times(1)).delete(groupMember);
}

@Test
public void testRemoveGroupMember_Unsuccessful_DashboardNotFound(){
    String username = "nonExistentUser";
    String memberName = "Alice";

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> groupMemberService.removeGroupMember(memberName, username));
}

@Test
public void testRemoveGroupMember_Unsuccessful_GroupMemberNotFound(){
    String username = "Jane";
    String memberName = "Alice";
    Dashboard janeDashboard = new Dashboard();
    janeDashboard.setId(1L);

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.of(janeDashboard));
    when(groupMemberRepository.findByDashboard_IdAndMemberName(janeDashboard.getId(), memberName)).thenReturn(Optional.empty()); //cannot find the member.

    assertThrows(GroupMemberNotFoundException.class, () -> groupMemberService.removeGroupMember(memberName, username));
}

@Test
public void testRemoveGroupMember_Unsuccessful_GroupMemberWithExpenses(){
    String username = "Jane";
    String memberName = "Alice";
    Dashboard janeDashboard = new Dashboard();
    janeDashboard.setId(1L);
    janeDashboard.setName("Jane's Dashboard");

    Expense expense1 = new Expense();
    expense1.setAmount(40.00);
    expense1.setType("Food");

    Expense expense2 = new Expense();
    expense2.setAmount(100.00);
    expense2.setType("Travel");

    // Associate expenses with the dashboard
    janeDashboard.setExpenses(new ArrayList<>(Arrays.asList(expense1, expense2))); //as long as there is expenses, i will be unable to delete the groupmembers

    // Create the group member
    GroupMember groupMember = new GroupMember();
    groupMember.setMemberName(memberName);
    groupMember.setDashboard(janeDashboard);
    groupMember.setBalance(50.0);

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.of(janeDashboard));
    when(groupMemberRepository.findByDashboard_IdAndMemberName(janeDashboard.getId(), memberName))
            .thenReturn(Optional.of(groupMember));

    assertThrows(IllegalStateException.class, () -> groupMemberService.removeGroupMember(memberName, username));
}

@Test
public void testGetAllGroupMembers_Successful(){
    String username = "Jane";
    Dashboard janeDashboard = new Dashboard();
    janeDashboard.setId(1L);

    GroupMember member1 = new GroupMember();
    member1.setMemberName("Jane");
    member1.setDashboard(janeDashboard);

    GroupMember member2 = new GroupMember();
    member2.setMemberName("John");
    member2.setDashboard(janeDashboard);

    GroupMember member3 = new GroupMember();
    member3.setMemberName("Doe");
    member3.setDashboard(janeDashboard);

    // Add them to a list
    List<GroupMember> members = Arrays.asList(member1, member2, member3);

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.of(janeDashboard));
    when(groupMemberRepository.findByDashboard_Id(janeDashboard.getId())).thenReturn(members);

    Set<String> result = groupMemberService.getAllGroupMembers(username);

    assertEquals(Set.of("Jane", "John", "Doe"), result);
    }
}
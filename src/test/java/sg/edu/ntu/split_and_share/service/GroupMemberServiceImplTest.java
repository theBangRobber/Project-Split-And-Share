package sg.edu.ntu.split_and_share.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.GroupMember;
import sg.edu.ntu.split_and_share.exception.DashboardNotFoundException;
import sg.edu.ntu.split_and_share.exception.DuplicateGroupMemberException;
import sg.edu.ntu.split_and_share.exception.GroupMemberNotFoundException;
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

private Dashboard mockDashboard;

@Test
void addGroupMembers_Successful() {
    String username = "testUser";
    Set<String> groupMembers = new HashSet<>(Arrays.asList("Alice", "Bob"));
    Dashboard dashboard = new Dashboard();
    dashboard.setId(1L);
    dashboard.setName("Test Dashboard");
    dashboard.setGroupMembers(new HashSet<>());

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.of(dashboard));
    when(groupMemberRepository.save(any(GroupMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Set<String> result = groupMemberService.addGroupMembers(groupMembers, username);

    assertEquals(groupMembers, result);
    verify(groupMemberRepository, times(2)).save(any(GroupMember.class));
}

@Test
public void testAddGroupMembers_Unsuccessful_DashboardNotFound(){
    String username = "testUser";
    Set<String> groupMembers = new HashSet<>(Arrays.asList("Alice", "Bob"));

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.empty());

    assertThrows(DashboardNotFoundException.class, () -> groupMemberService.addGroupMembers(groupMembers, username));
}

@Test
public void testAddGroupMembers_Unsuccessful_DuplicatedGroupMember(){
    String username = "testUser";
    Set<String> groupMembers = new HashSet<>(Collections.singletonList("Alice"));
    Dashboard dashboard = new Dashboard();
    dashboard.setId(1L);
    dashboard.setName("Test Dashboard");

    GroupMember existingMember = new GroupMember();
    existingMember.setMemberName("Alice");
    dashboard.setGroupMembers(Set.of(existingMember));

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.of(dashboard));

    assertThrows(DuplicateGroupMemberException.class, () -> groupMemberService.addGroupMembers(groupMembers, username));
}

@Test
public void testRemoveGroupMember_Successful(){
    String username = "testUser";
    String memberName = "Alice";
    Dashboard dashboard = new Dashboard();
    dashboard.setId(1L);
    dashboard.setName("Test Dashboard");

    GroupMember groupMember = new GroupMember();
    groupMember.setMemberName(memberName);
    groupMember.setDashboard(dashboard);
    groupMember.setBalance(0.0);

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.of(dashboard));
    when(groupMemberRepository.findByDashboard_IdAndMemberName(dashboard.getId(), memberName))
            .thenReturn(Optional.of(groupMember));

    groupMemberService.removeGroupMember(memberName, username);

    verify(groupMemberRepository, times(1)).delete(groupMember);
}

@Test
public void testRemoveGroupMember_Unsuccessful_DashboardNotFound(){
    String username = "testUser";
    String memberName = "Alice";

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.empty());

    assertThrows(DashboardNotFoundException.class, () -> groupMemberService.removeGroupMember(memberName, username));
}

@Test
public void testRemoveGroupMember_Unsuccessful_GroupMemberNotFound(){
    String username = "testUser";
    String memberName = "Alice";
    Dashboard dashboard = new Dashboard();
    dashboard.setId(1L);

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.of(dashboard));
    when(groupMemberRepository.findByDashboard_IdAndMemberName(dashboard.getId(), memberName)).thenReturn(Optional.empty());

    assertThrows(GroupMemberNotFoundException.class, () -> groupMemberService.removeGroupMember(memberName, username));
}

@Test
public void testRemoveGroupMember_Unsuccessful_GroupMemberWithExpenses(){

}

@Test
public void testGetAllGroupMembers_Successful(){
    String username = "testUser";
    Dashboard dashboard = new Dashboard();
    dashboard.setId(1L);

    List<GroupMember> members = Arrays.asList(
            new GroupMember("Alice", dashboard),
            new GroupMember("Bob", dashboard)
    );

    when(dashboardRepository.findByUser_Username(username)).thenReturn(Optional.of(dashboard));
    when(groupMemberRepository.findByDashboard_Id(dashboard.getId())).thenReturn(members);

    Set<String> result = groupMemberService.getAllGroupMembers(username);

    assertEquals(Set.of("Alice", "Bob"), result);
}

@Test
public void testGetAllGroupMembers_Unsuccessful_DashboardNotFound(){

}

}

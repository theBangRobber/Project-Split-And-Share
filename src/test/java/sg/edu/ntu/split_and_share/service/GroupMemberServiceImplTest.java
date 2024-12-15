package sg.edu.ntu.split_and_share.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.GroupMember;
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

@BeforeEach
public void setup(){

    mockDashboard = new Dashboard();
    mockDashboard.setGroupMembers(new ArrayList<>()); // i need to have group members

    // Add mock group members to the dashboard
    GroupMember memberJane = new GroupMember();
    memberJane.setMemberName("Jane");

    GroupMember memberDoe = new GroupMember();
    memberDoe.setMemberName("Doe");

    GroupMember memberJohn = new GroupMember();
    memberJohn.setMemberName("John");

    mockDashboard.getGroupMembers().addAll(List.of(memberJane,memberDoe,memberJohn)); //creating group member with the above

}

@Test
public void testAddGroupMembers_Successful(){

    when(dashboardRepository.findByUser_Username("Jane")).thenReturn(Optional.of(mockDashboard));

    when(groupMemberRepository.save(any(GroupMember.class))).thenAnswer(invocation -> {
        GroupMember memberElle = invocation.getArgument(0);
        memberElle.setMemberName("Elle");
        memberElle.setBalance(0.0);
        memberElle.setDashboard(mockDashboard);
        return memberElle;
    });

    // GroupMember addGroupMember = groupMemberService.addGroupMembers(groupMemberList, username)

    
}

@Test
public void testAddGroupMembers_Unsuccessful_DashboardNotFound(){

}

@Test
public void testAddGroupMembers_Unsuccessful_DuplicatedGroupMember(){

}

@Test
public void testRemoveGroupMember_Successful(){

}

@Test
public void testRemoveGroupMember_Unsuccessful_DashboardNotFound(){

}

@Test
public void testRemoveGroupMember_Unsuccessful_GroupMemberNotFound(){

}

@Test
public void testRemoveGroupMember_Unsuccessful_GroupMemberWithExpenses(){

}

@Test
public void testGetAllGroupMembers_Successful(){

}

@Test
public void testGetAllGroupMembers_Unsuccessful_DashboardNotFound(){

}

}

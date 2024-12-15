package sg.edu.ntu.split_and_share.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.entity.GroupMember;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;



@ExtendWith(MockitoExtension.class) 
public class DashboardServiceImplTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private Dashboard mockDashboard;

    @BeforeEach
    void setUp() {

    // Prepare a mock dashboard
    mockDashboard = new Dashboard();
    mockDashboard.setExpenses(new ArrayList<>()); //i need to set new array of expenses
    mockDashboard.setGroupMembers(new ArrayList<>()); // i need to have group members

    // Add mock group members to the dashboard
    GroupMember memberJane = new GroupMember();
    memberJane.setMemberName("Jane");
    memberJane.setExpenses(new ArrayList<>());

    GroupMember memberDoe = new GroupMember();
    memberDoe.setMemberName("Doe");

    GroupMember memberJohn = new GroupMember();
    memberJohn.setMemberName("John");

     // Add mock group members
     mockDashboard.getGroupMembers().addAll(List.of(memberJane,memberDoe,memberJohn)); //creating group member with the above

    // Create mock expenses
    //Expense 1: expenses paid by john and shared by two member - jane and doe
    Expense expense1 = new Expense();
    expense1.setType("Food");
    expense1.setAmount(20.0);
    expense1.setPaidBy("John");
    expense1.setSharedBy(new HashSet<>(List.of(memberJane, memberDoe))); // set share

    //Expense 2: expenses paid by jane and shared by two member - john and doe
    Expense expense2 = new Expense();
    expense2.setType("Travel");
    expense2.setAmount(50.0);
    expense1.setPaidBy("Jane");
    expense1.setSharedBy(new HashSet<>(List.of(memberJohn, memberDoe)));

    // Assign expenses to members
    memberJane.setExpenses(Set.of(expense1, expense2)); // Jane shares expense1 and pays for expense2
    memberDoe.setExpenses(Set.of(expense1, expense2));  // Doe shares both expenses
    memberJohn.setExpenses(Set.of(expense2));  

    // Add expenses to the mock dashboard
    mockDashboard.getExpenses().addAll(List.of(expense1, expense2));

}

 @Test
    void calculateTotalSum_ShouldReturnCorrectSum() {
        when(dashboardRepository.findByUser_Username("user1"))
                .thenReturn(Optional.of(mockDashboard));

        double totalSum = dashboardService.calculateTotalSum("user1");

        assertEquals(70.0, totalSum);
        verify(dashboardRepository, times(1)).findByUser_Username("user1");
    }

    @Test
    public void testCalculateTotalSum_Unsuccessful_DashboardNotFound (){

    //   User existingUser = User.builder().id(1L).username("Mmanyuu").password("123456789").name("Manyu").dashboard(null).build();

    //   when(dashboardRepository.findByUser_Username("Mmanyuu")).thenReturn(Optional.of(existingUser));

    //     Exception exception = assertThrows(DashboardNotFoundException.class, ()->dashboardService.calculateTotalSum())
    // }
}
}
package sg.edu.ntu.split_and_share.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import sg.edu.ntu.split_and_share.exception.DashboardNotFoundException;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;



@ExtendWith(MockitoExtension.class) 
public class DashboardServiceImplTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private Dashboard mockDashboard;

    @BeforeEach //Initial set up on mock dashboard, expenses and groupmember - let me think through if need refractor to simulate a more close set up to our code (i.e. cause dashboard is created when i create the user, hence was thinking if i should mock user as well with dashboard in. but it will muddle the purpose of the test cause i don't need any of user information)
    public void setUp() {

    // Prepare a mock dashboard - this is a mockDashboard of "existingUser" as i understand the app works in a way that the user dont need to be a group member and can also handle expenses. but this time i will change it to "Jane" so that its easier to understand
    mockDashboard = new Dashboard();
    mockDashboard.setExpenses(new ArrayList<>()); //i need to set new array of expenses
    mockDashboard.setGroupMembers(new ArrayList<>()); // i need to have group members

    // Add mock group members to the dashboard
    GroupMember memberJane = new GroupMember();
    memberJane.setMemberName("Jane");

    GroupMember memberDoe = new GroupMember();
    memberDoe.setMemberName("Doe");

    GroupMember memberJohn = new GroupMember();
    memberJohn.setMemberName("John");

    // Create mock expenses
    //Expense 1: expenses paid by john and shared by two member - jane and doe
    Expense expense1 = new Expense();
    expense1.setType("Food");
    expense1.setAmount(120.0);
    expense1.setPaidBy("Jane");
    expense1.setSharedBy(new HashSet<>(List.of(memberJane, memberDoe))); // set share

    //Expense 2: expenses paid by jane and shared by two member - john and doe
    Expense expense2 = new Expense();
    expense2.setType("Travel");
    expense2.setAmount(50.0);
    expense2.setPaidBy("Doe");
    expense2.setSharedBy(new HashSet<>(List.of(memberJohn, memberDoe)));

    // Assign expenses to members
    memberJane.setExpenses(Set.of(expense1, expense2)); // Jane shares expense1 and pays for expense2
    memberDoe.setExpenses(Set.of(expense1, expense2));  // Doe shares both expenses but didnt pay any
    memberJohn.setExpenses(Set.of(expense2));  //John only shares expense 2 and pays for expenses 1

    // Add mock group members
    mockDashboard.getGroupMembers().addAll(List.of(memberJane,memberDoe,memberJohn)); //creating group member with the above

    // Add expenses to the mock dashboard
    mockDashboard.getExpenses().addAll(List.of(expense1, expense2));
}

 @Test
    void testCalculateTotalSum_ShouldReturnCorrectSum() {
        when(dashboardRepository.findByUser_Username("Jane")) //this username is the existing user's dashboard
                .thenReturn(Optional.of(mockDashboard));

        double totalSum = dashboardService.calculateTotalSum("Jane");

        assertEquals(170.0, totalSum);
        verify(dashboardRepository, times(1)).findByUser_Username("Jane");
    }

    @Test
    void testCalculateTotalSum_ThrowException_DashboardNotFound() {
        when(dashboardRepository.findByUser_Username("nonExistenceUser"))
                .thenReturn(Optional.empty());

        assertThrows(DashboardNotFoundException.class,
                () -> dashboardService.calculateTotalSum("nonExistenceUser"));

        verify(dashboardRepository, times(1)).findByUser_Username("nonExistenceUser");
    }

    @Test
    void testSumExpensesByType_ShouldReturnCorrectMap() {
        when(dashboardRepository.findByUser_Username("Jane"))
                .thenReturn(Optional.of(mockDashboard));

        Map<String, Double> expensesByType = dashboardService.sumExpensesByType("Jane");

        assertEquals(2, expensesByType.size());
        assertEquals(120.0, expensesByType.get("Food"));
        assertEquals(50.0, expensesByType.get("Travel"));
        verify(dashboardRepository, times(1)).findByUser_Username("Jane");
    }

    @Test
    void testSumExpensesByType_ShouldReturnEmptyMap_WhenNoExpenses() {
        //mock the dashboard to be emptylist using Collection imported from util. Collection is an interface allowing item to be group within a single container. and this set the expenses to be empty using its method of emptylist().
        mockDashboard.setExpenses(Collections.emptyList());

        when(dashboardRepository.findByUser_Username("otherUser"))//this is another user's dashboard with no expenses
                .thenReturn(Optional.of(mockDashboard));

        Map<String, Double> expensesByType = dashboardService.sumExpensesByType("otherUser");

        assertTrue(expensesByType.isEmpty());
        verify(dashboardRepository, times(1)).findByUser_Username("otherUser");
    }

    @Test
    void testCountExpensesByType_ShouldReturnCorrectCount() {
        when(dashboardRepository.findByUser_Username("Jane"))
                .thenReturn(Optional.of(mockDashboard));

        Map<String, Long> expenseCountByType = dashboardService.countExpensesByType("Jane");

        assertEquals(2, expenseCountByType.size());
        assertEquals(1, expenseCountByType.get("Food"));
        assertEquals(1, expenseCountByType.get("Travel"));
        verify(dashboardRepository, times(1)).findByUser_Username("Jane");
    }

    @Test
    void testCountTotalNumberOfExpenses_ShouldReturnCorrectCount() {
        when(dashboardRepository.findByUser_Username("Jane"))
                .thenReturn(Optional.of(mockDashboard));

        long totalExpenses = dashboardService.countTotalNumberOfExpenses("Jane");

        assertEquals(2, totalExpenses);
        verify(dashboardRepository, times(1)).findByUser_Username("Jane");
    }

    @Test //was wondering if there is a better way to do this test instead of myself calculating the expected. hmm..
    void testCalculateNetBalances_ShouldReturnCorrectBalances() {
        // Arrange: Mock the dashboard for "Jane" user
        when(dashboardRepository.findByUser_Username("Jane"))
                .thenReturn(Optional.of(mockDashboard));

        // Act: Call the method under test to calculate the balances
        Map<String, BigDecimal> balances = dashboardService.calculateNetBalances("Jane");

        // Assert: Check that the returned balances are correct
        assertEquals(3, balances.size());

        assertEquals(BigDecimal.valueOf(-25).setScale(2), balances.get("John"),"John should owe Doe 25 bucks");  
        assertEquals(BigDecimal.valueOf(60).setScale(2), balances.get("Jane"), "Jane should be paid 60, and by Doe");
        assertEquals(BigDecimal.valueOf(-35).setScale(2), balances.get("Doe"),"Doe should pay Jane 60 and given back 25 by John");

        verify(dashboardRepository, times(1)).findByUser_Username("Jane");


        // Verify that the repository's find method was called exactly once for "Jane"
        verify(dashboardRepository, times(1)).findByUser_Username("Jane");
    }

    @Test
    void testCalculateNetBalances_ShouldReturnZeroBalances_WhenNoExpenses() {

        mockDashboard.setExpenses(Collections.emptyList()); //User had no expenses

        when(dashboardRepository.findByUser_Username("otherUser"))
                .thenReturn(Optional.of(mockDashboard));

        Map<String, BigDecimal> balances = dashboardService.calculateNetBalances("otherUser");

        System.out.println(balances);

        //This is to verify individual members balances is zero.
        for (GroupMember member : mockDashboard.getGroupMembers()) {
                assertEquals(BigDecimal.ZERO, balances.get(member.getMemberName()));
            }
        
        // Verify that the repository method was called once with the username "otherUser"
        verify(dashboardRepository, times(1)).findByUser_Username("otherUser");
    }

    @Test
    void testGetAllIndividualExpenses_ShouldReturnCorrectList() {
        when(dashboardRepository.findByUser_Username("Jane"))
                .thenReturn(Optional.of(mockDashboard));

        List<Expense> expenses = dashboardService.getAllIndividualExpenses("Jane");

        assertEquals(2, expenses.size());
        verify(dashboardRepository, times(1)).findByUser_Username("Jane");
    }

    @Test
    void testGetAllIndividualExpenses_ShouldReturnEmptyList_WhenNoExpenses() {
        mockDashboard.setExpenses(Collections.emptyList());
        when(dashboardRepository.findByUser_Username("otherUser"))
                .thenReturn(Optional.of(mockDashboard));

        List<Expense> expenses = dashboardService.getAllIndividualExpenses("otherUser");

        assertTrue(expenses.isEmpty());
        verify(dashboardRepository, times(1)).findByUser_Username("otherUser");
    }

    @Test
    void testResetDashboard_ShouldClearExpensesAndGroupMembers() {
        when(dashboardRepository.findByUser_Username("Jane"))
                .thenReturn(Optional.of(mockDashboard));

        dashboardService.resetDashboard("Jane");

        assertTrue(mockDashboard.getExpenses().isEmpty());
        assertTrue(mockDashboard.getGroupMembers().isEmpty());
        verify(dashboardRepository, times(1)).save(mockDashboard);
    }

    @Test
    void testResetDashboard_ShouldThrowException_WhenDashboardNotFound() {
        when(dashboardRepository.findByUser_Username("nonExistenceUser"))
                .thenReturn(Optional.empty());

        assertThrows(DashboardNotFoundException.class,
                () -> dashboardService.resetDashboard("nonExistenceUser"));

        verify(dashboardRepository, times(1)).findByUser_Username("nonExistenceUser");
    }
}
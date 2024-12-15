package sg.edu.ntu.split_and_share.service;

import jakarta.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.entity.GroupMember;
import sg.edu.ntu.split_and_share.exception.DashboardNotFoundException;
import sg.edu.ntu.split_and_share.exception.ExpenseNotFoundException;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;
import sg.edu.ntu.split_and_share.repository.ExpenseRepository;
import sg.edu.ntu.split_and_share.repository.GroupMemberRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
public class ExpenseServiceImplTest {

@Mock
private ExpenseRepository expenseRepository;

@Mock
  private DashboardRepository dashboardRepository;

  @Mock
  private GroupMemberRepository groupMemberRepository;

  @Mock
  private EntityManager entityManager;

  @InjectMocks
  private ExpenseServiceImpl expenseService;
   
  
  @Test
  void testAddExpense_Successful() {
    // Arrange - Mock expense, group member and sharedby
    Expense newExpense = new Expense();
    newExpense.setAmount(100.0);

    GroupMember member = new GroupMember();
    member.setMemberName("Member1");
    member.setBalance(0.0);

    Set<GroupMember> sharedBy = new HashSet<>();
    sharedBy.add(member);

    newExpense.setSharedBy(sharedBy);

    //Arrange - Mock dashboard
    Dashboard dashboard = new Dashboard();
    dashboard.setId(1L);

    //Arrange - Mock repo beheavior. we need to find dashboard by username, save a group member into the dashboard and save the expense into the dashboard with the group member
    when(dashboardRepository.findByUser_Username("Mmanyuu")).thenReturn(Optional.of(dashboard));
    when(groupMemberRepository.findByMemberName("Member1")).thenReturn(Optional.of(member));
    when(groupMemberRepository.save(any(GroupMember.class))).thenAnswer(invocation -> invocation.getArgument(0)); //returning this without setting any parameters mean i am just simulating a real beviour of the method save (returning the object as it is)
    when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Expense result = expenseService.addExpense(newExpense, "Mmanyuu");

    // Assert and Verify
    assertNotNull(result);
    assertEquals(dashboard, result.getDashboard());
    verify(expenseRepository, times(1)).save(newExpense);
    verify(groupMemberRepository, times(1)).save(member);
  }

  @Test
  void testAddExpense_Unsuccessful_DashboardNotFound() {
      // Arrange - mock expense
      Expense newExpense = new Expense();
      newExpense.setAmount(100.0);

      //Mock behevior of repo when find dashboard by username, its not there
      when(dashboardRepository.findByUser_Username("nonexistentdashboard")).thenReturn(Optional.empty());

      // Act & Assert
      assertThrows(DashboardNotFoundException.class, () -> expenseService.addExpense(newExpense, "nonexistentdashboard"));
  }

  @Test
  void testGetExpense_Successful() {
      // Arrange - mock expense
      Expense existingExpense = new Expense();
      existingExpense.setId(1L);

      //Arrange - mock behevior of expense, it return a optional object
      when(expenseRepository.findById(1L)).thenReturn(Optional.of(existingExpense));

      // Act - execute method
      Expense result = expenseService.getExpense(1L);

      // Assert
      assertNotNull(result);
      assertEquals(1L, result.getId());
  }

  @Test
  void testGetExpense_Unsuccessful_NotFound() {
      //Arrange - mock behevior of repo when find expense by id, its not there
      when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

      // Act & Assert
      assertThrows(ExpenseNotFoundException.class, () -> expenseService.getExpense(1L));
  }

  @Test
  void testGetAllExpenses() {
      // Arrange
      List<Expense> expenses = Arrays.asList(new Expense(), new Expense());

      when(expenseRepository.findAll()).thenReturn(expenses);

      // Act
      List<Expense> result = expenseService.getAllExpenses();

      // Assert
      assertEquals(2, result.size());
      verify(expenseRepository, times(1)).findAll();
  }

  @Test
  void testUpdateExpense_Successful() {
      // Arrange - mock existing expenses information
      Expense existingExpense = new Expense();
      existingExpense.setId(1L);
      existingExpense.setAmount(100.0);
      existingExpense.setSharedBy(new HashSet<>());
      existingExpense.setType("Food");
      existingExpense.setPaidBy("User1");

      // Arrange - mock new details to updated
      Expense newDetails = new Expense();
      newDetails.setAmount(200.0);
      newDetails.setSharedBy(new HashSet<>());
      newDetails.setType("Travel");
      newDetails.setPaidBy("User2");

      //Arrange - mock the behavior of repo. we need to find by id to get existing expenses and the save the new details.
      when(expenseRepository.findById(1L)).thenReturn(Optional.of(existingExpense));
      when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

      // Act - execute method
      Expense result = expenseService.updateExpense(1L, newDetails);

      // Assert
      assertNotNull(result);
      assertEquals(200.0, result.getAmount());
      verify(expenseRepository, times(1)).save(existingExpense);
  }

  @Test
  void testUpdateExpense_Unsuccessful_NotFound() {
      // Arrange
      when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

      // Act & Assert
      assertThrows(ExpenseNotFoundException.class, () -> expenseService.updateExpense(1L, new Expense()));
  }

  @Test
  void testDeleteExpense_Successful() {
      // Arrange
      Expense existingExpense = new Expense();
      existingExpense.setAmount(100.0);
      existingExpense.setId(1L);

      GroupMember member = new GroupMember();
      member.setMemberName("Member1");
      member.setBalance(0.0);
  
      Set<GroupMember> sharedBy = new HashSet<>();
      sharedBy.add(member);
  
      existingExpense.setSharedBy(sharedBy);
  
      //Arrange - Mock dashboard
      Dashboard dashboard = new Dashboard();
      dashboard.setId(1L);

      when(entityManager.find(Expense.class, 1L)).thenReturn(existingExpense);

      // Act
      expenseService.deleteExpense(1L);

      // Assert
      verify(entityManager, times(1)).remove(existingExpense);
  }

  @Test
  void testDeleteExpense_Unsuccuessful_NotFound() {
      // Arrange
      when(entityManager.find(Expense.class, 1L)).thenReturn(null);

      // Act & Assert
      assertThrows(ExpenseNotFoundException.class, () -> expenseService.deleteExpense(1L));
  }
}
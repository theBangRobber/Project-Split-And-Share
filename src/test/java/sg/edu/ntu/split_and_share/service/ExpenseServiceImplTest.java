package sg.edu.ntu.split_and_share.service;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;
import sg.edu.ntu.split_and_share.repository.ExpenseRepository;
import sg.edu.ntu.split_and_share.repository.GroupMemberRepository;


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
   
  @Test //understand this method is to add expenses to active dashboard instead of tagging it to individual user's dashboard
  public void testAddExpense_Successful() {
    //Arrange. the minimum requirement to test add expenses is expense, sharedby and dashboard
    Expense expense = new Expense();
    expense.setAmount(100.0);
    expense.setSharedBy(new HashSet<>());

    Dashboard dashboard = new Dashboard();
    dashboard.setId(1L);

    when(dashboardRepository.findByUser_Username("Mmanyuu")).thenReturn(Optional.of(dashboard));
    when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Expense result = expenseService.addExpense(expense, "Mmanyuu");
    System.out.println(result.getAmount());

    assertNotNull(result,"result should not be null");
    assertEquals(dashboard,result.getDashboard());

    
  }

  public void testAddExpense_Unsuccessful_DashboardNotFound() {
    
  }

  
}

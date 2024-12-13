package sg.edu.ntu.split_and_share.service;

import java.util.List;
import java.util.Map;

import sg.edu.ntu.split_and_share.entity.Expense;

public interface DashboardService {

  // Calculate the total sum of all expenses
  double calculateTotalSum(String username);

  // Sum of expenses by type
  Map<String, Double> sumExpensesByType(String username);

  // Count the number of expenses by type
  Map<String, Long> countExpensesByType(String username);

  // Grand total number of expenses
  long countTotalNumberOfExpenses(String username);

  // Calculate individual/group member balances
  Map<String, Double> calculateNetBalances(String username);

  // Fetch all individual expenses
  List<Expense> getAllIndividualExpenses(String username);

  // Reset dashboard entirely
  void resetDashboard(String username);
}

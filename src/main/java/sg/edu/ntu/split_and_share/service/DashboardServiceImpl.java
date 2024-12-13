package sg.edu.ntu.split_and_share.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.entity.GroupMember;
import sg.edu.ntu.split_and_share.exception.DashboardNotFoundException;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;

import jakarta.transaction.Transactional;

@Service
public class DashboardServiceImpl implements DashboardService {

  private DashboardRepository dashboardRepository;
  private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

  public DashboardServiceImpl(DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  // Calculate the grand total of all expenses
  @Override
  public double calculateTotalSum(String username) {
    logger.info("Calculating total sum of expenses for username: {}", username);
    // Fetch the dashboard by username
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("Dashboard not found for user: {}", username);
          return new DashboardNotFoundException();
        });

    // Calculate the total sum of all expenses
    double totalSum = dashboard.getExpenses().stream()
        .mapToDouble(Expense::getAmount)
        .sum();

    logger.info("Total sum of expenses for username {} is: {}", username, totalSum);
    return totalSum;
  }

  // Calculate total sum of each expense type
  @Override
  public Map<String, Double> sumExpensesByType(String username) {
    logger.info("Calculating total sum of each expense type for username: {}", username);
    // Fetch the dashboard
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("Dashboard not found for username: {}", username);
          return new DashboardNotFoundException();
        });

    // Group and sum expenses by type
    Map<String, Double> expensesByType = dashboard.getExpenses().stream()
        .collect(Collectors.groupingBy(Expense::getType,
            Collectors.summingDouble(Expense::getAmount)));

    logger.info("Expenses grouped by type for username {}: {}", username, expensesByType);
    return expensesByType;
  }

  // Count the number of each expense type
  @Override
  public Map<String, Long> countExpensesByType(String username) {
    logger.info("Counting expenses by type for username: {}", username);
    // Fetch the dashboard
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("Dashboard not found for username: {}", username);
          return new DashboardNotFoundException();
        });

    // Count expenses by type
    Map<String, Long> expenseCountByType = dashboard.getExpenses().stream()
        .collect(Collectors.groupingBy(Expense::getType, Collectors.counting()));

    logger.info("Count of expenses by type for username {}: {}", username, expenseCountByType);
    return expenseCountByType;
  }

  // Grand total number of expenses
  @Override
  public long countTotalNumberOfExpenses(String username) {
    logger.info("Counting total number of expenses for username: {}", username);
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("Dashboard not found for username: {}", username);
          return new DashboardNotFoundException();
        });

    long totalNumberOfExpenses = dashboard.getExpenses().size();

    logger.info("Total number of expenses for username {}: {}", username, totalNumberOfExpenses);
    return totalNumberOfExpenses;
  }

  // Calculate individual/group member balances
  @Override
  public Map<String, Double> calculateNetBalances(String username) {
    logger.info("Calculating net balances for username: {}", username);
    // Fetch the dashboard
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("Dashboard not found for username: {}", username);
          return new DashboardNotFoundException();
        });

    Map<String, Double> balances = new HashMap<>();

    for (Expense expense : dashboard.getExpenses()) {
      String payer = expense.getPaidBy();

      // Map sharedBy from GroupMember to their names
      Set<String> sharers = expense.getSharedBy()
          .stream()
          .map(GroupMember::getMemberName)
          .collect(Collectors.toSet());

      // Payer's balance
      balances.put(payer, balances.getOrDefault(payer, 0.0) + expense.getAmount());

      // Shared members' balances
      double shareAmount = expense.getAmount() / sharers.size();
      for (String sharer : sharers) {
        balances.put(sharer, balances.getOrDefault(sharer, 0.0) - shareAmount);
      }
    }

    logger.info("Calculated net balances for username {}: {}", username, balances);
    return balances;
  }

  // Fetch all individual expenses
  @Override
  public List<Expense> getAllIndividualExpenses(String username) {
    logger.info("Fetching all individual expenses for username: {}", username);
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("Dashboard not found for username: {}", username);
          return new DashboardNotFoundException();
        });

    List<Expense> expenses = dashboard.getExpenses();
    logger.info("Fetched {} expenses for username: {}", expenses.size(), username);
    return expenses;
  }

  // Reset dashboard
  @Transactional
  @Override
  public void resetDashboard(String username) {
    logger.info("Resetting dashboard for username: {}", username);
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("Dashboard not found for username: {}", username);
          return new DashboardNotFoundException();
        });

    // Clear the lists of expenses and group members
    dashboard.getExpenses().clear();
    dashboard.getGroupMembers().clear();

    dashboardRepository.save(dashboard);
    logger.info("Dashboard reset successfully for username: {}", username);
  }
}

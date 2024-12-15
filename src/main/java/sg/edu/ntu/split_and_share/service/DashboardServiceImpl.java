package sg.edu.ntu.split_and_share.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.entity.GroupMember;
import sg.edu.ntu.split_and_share.exception.DashboardNotFoundException;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

  private DashboardRepository dashboardRepository;
  private static final Logger logger = LoggerFactory.getLogger(DashboardServiceImpl.class);

  public DashboardServiceImpl(DashboardRepository dashboardRepository) {
    this.dashboardRepository = dashboardRepository;
  }

  // Get total sum of all expenses
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

  // Get total sum of each expense type
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

  // Get total number of expenses
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

  // Calculate individual member's net balances
  @Override
  public Map<String, BigDecimal> calculateNetBalances(String username) {
    logger.info("Calculating net balances for all members on {}", username + "'s dashboard");
    // Fetch the dashboard
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("Dashboard not found for username: {}", username);
          return new DashboardNotFoundException();
        });
    // Initialize balances with all group members set to BigDecimal.ZERO
    Map<String, BigDecimal> balances = new HashMap<>();
    for (GroupMember member : dashboard.getGroupMembers()) {
      balances.put(member.getMemberName(), BigDecimal.ZERO);
    }
    logger.info("Initialized balances for all group members to zero.");

    for (Expense expense : dashboard.getExpenses()) {
      String payer = expense.getPaidBy();
      logger.info("Processing expense paid by: {}", payer);

      // Map sharedBy from GroupMember to their names
      // Converts the set of GroupMember objects to a set of member names
      Set<String> sharers = expense.getSharedBy()
          .stream()
          .map(GroupMember::getMemberName)
          .collect(Collectors.toSet());
      logger.info("Expense shared by: {}", sharers);

      // Calculate the share amount using BigDecimal for precision
      BigDecimal totalAmount = BigDecimal.valueOf(expense.getAmount());
      BigDecimal individualShare = totalAmount
          .divide(BigDecimal.valueOf(sharers.size()), 2, RoundingMode.HALF_UP);
      logger.info("Total amount: {}, Individual share: {}", totalAmount, individualShare);

      // Update payer's balance
      // Retrieves the current balance for the payer or defaults to 0.0 if not present
      // Adds the payer's net contribution to their balance
      BigDecimal payerBalance = balances.getOrDefault(payer, BigDecimal.ZERO)
          .add(totalAmount.subtract(individualShare));
      balances.put(payer, payerBalance.setScale(2, RoundingMode.HALF_UP));
      logger.info("Updated balance for payer '{}': {}", payer, payerBalance.setScale(2, RoundingMode.HALF_UP));

      // Update shared members' balances
      for (String sharer : sharers) {
        // Ensures the payer's balance is not updated again.
        if (!sharer.equals(payer)) {
          // Retrieves the current balance for the sharer and subtracts their share of the
          // expense.
          BigDecimal sharerBalance = balances.getOrDefault(sharer, BigDecimal.ZERO)
              .subtract(individualShare);
          balances.put(sharer, sharerBalance.setScale(2, RoundingMode.HALF_UP));
          logger.info("Updated balance for sharer '{}': {}", sharer, sharerBalance.setScale(2, RoundingMode.HALF_UP));
        }
      }
    }

    logger.info("Calculated net balances for username all members: {}", balances);
    return balances;
  }

  // Settle balance among group members
  @Override
  @Transactional
  public Map<String, List<String>> settleBalances(String username) {
    logger.info("Calculating settlement transactions for all members on {}'s dashboard", username);

    // Calculate net balances for each member
    Map<String, BigDecimal> netBalances = calculateNetBalances(username);
    logger.info("Net balances calculated: {}", netBalances);

    // Initialize PriorityQueues for debtors and creditors
    // PriorityQueue class is part of the Java Collections Framework and is used to
    // maintain a collection of elements where each element has a priority.
    // Here I use PriorityQueue to sort debtors and creditors, Comparator will
    // decide the order in the PriorityQueue
    // Debtors - members who owe money, ordered by the smallest debt first
    // Creditors - members who are owed money, ordered by the largest credit first
    PriorityQueue<MemberBalance> debtors = new PriorityQueue<>(Comparator.comparing(MemberBalance::getAmount));
    PriorityQueue<MemberBalance> creditors = new PriorityQueue<>(
        Comparator.comparing(MemberBalance::getAmount).reversed());

    // Separate members into debtors and creditors
    netBalances.forEach((member, balance) -> {
      if (balance.compareTo(BigDecimal.ZERO) > 0) {
        creditors.add(new MemberBalance(member, balance));
      } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
        debtors.add(new MemberBalance(member, balance.negate()));
      }
    });

    // Map to store settlement instructions
    Map<String, List<String>> settlements = new HashMap<>();

    // Match debtors to creditors
    // This while loop will keep going until both debtors and creditors have zero
    // balance
    // poll() removes any debtor or creditor once their balance is zero, and then
    // retrieves the next member from their respective PriorityQueue
    while (!debtors.isEmpty() && !creditors.isEmpty()) {
      MemberBalance debtor = debtors.poll();
      MemberBalance creditor = creditors.poll();

      // Log the current debtor and creditor
      logger.info("Processing debtor: {}", debtor);
      logger.info("Processing creditor: {}", creditor);

      // min() method is provided by BigDecimal, it compares two BigDecimal values and
      // returns the smaller one
      // the usage of min() here is because of two factors
      // 1 - The debtor can only pay up to what they owe
      // 2 - The creditor can only receive up to what they are owed
      // If the debtor has a smaller amount than creditor, the debtor can only pay up
      // what they owe
      // If the creditor has smaller amount than debtor, the creditor can only receive
      // up to what they are owed.
      // reset debtor's and creditor's balance after each settlement
      BigDecimal settleAmount = debtor.getAmount().min(creditor.getAmount());
      debtor.setAmount(debtor.getAmount().subtract(settleAmount));
      creditor.setAmount(creditor.getAmount().subtract(settleAmount));

      // Set settlement string as legible output for user
      // It generates a string describing the settlement transaction and adds it to a
      // list of settlements for the debtor.
      // output example - "Pay 30.00 to Johnny"
      // Create new key of debtor.getMember() in the Map if it does not not already
      // exists
      String settlement = String.format("Pay %.2f to %s", settleAmount, creditor.getMember());
      settlements.computeIfAbsent(debtor.getMember(), key -> new ArrayList<>()).add(settlement);

      logger.info("{} should pay {} to {}", debtor.getMember(), settleAmount, creditor.getMember());

      // Add remaining balance back to queues if any
      if (debtor.getAmount().compareTo(BigDecimal.ZERO) > 0) {
        debtors.add(debtor);
      }
      if (creditor.getAmount().compareTo(BigDecimal.ZERO) > 0) {
        creditors.add(creditor);
      }
    }

    logger.info("Calculated settlements for all members: {}", settlements);
    return settlements;
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
    // Fetch the dashboard
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("Dashboard not found for username: {}", username);
          return new DashboardNotFoundException();
        });
    logger.info("Fetched dashboard for username: {}", username);

    // Remove all expenses
    if (dashboard.getExpenses() != null) {
      dashboard.getExpenses().forEach(expense -> expense.setDashboard(null));
      dashboard.getExpenses().clear();
      logger.info("Cleared all expenses for dashboard of username: {}", username);
    }

    // Remove all group members
    if (dashboard.getGroupMembers() != null) {
      dashboard.getGroupMembers().forEach(member -> member.setDashboard(null));
      dashboard.getGroupMembers().clear();
      logger.info("Cleared all group members for dashboard of username: {}", username);
    }

    // Save the dashboard to persist changes
    dashboardRepository.save(dashboard);
    logger.info("Dashboard reset successfully for username: {}", username);
  }
}

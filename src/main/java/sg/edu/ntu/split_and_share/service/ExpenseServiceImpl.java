package sg.edu.ntu.split_and_share.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.exception.DashboardNotFoundException;
import sg.edu.ntu.split_and_share.exception.ExpenseNotFoundException;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;
import sg.edu.ntu.split_and_share.repository.ExpenseRepository;

import jakarta.transaction.Transactional;

@Service
public class ExpenseServiceImpl implements ExpenseService {

  private ExpenseRepository expenseRepository;
  private DashboardRepository dashboardRepository;
  private static final Logger logger = LoggerFactory.getLogger(ExpenseServiceImpl.class);

  public ExpenseServiceImpl(ExpenseRepository expenseRepository, DashboardRepository dashboardRepository) {
    this.expenseRepository = expenseRepository;
    this.dashboardRepository = dashboardRepository;
  }

  // Create new expense
  @Transactional
  @Override
  public Expense addExpense(Expense expense, String username) {
    // Fetch the dashboard
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("No dashboard found in the database");
          return new DashboardNotFoundException();
        });

    // Attach the expense to the dashboard
    expense.setDashboard(dashboard);

    // Save the expense
    Expense savedExpense = expenseRepository.save(expense);

    // Log the success
    logger.info("Successfully added expense with ID: {}", savedExpense.getId());

    return savedExpense;
  }

  // Get an expense
  @Override
  public Expense getExpense(Long id) {
    // Fetch and return the expense
    return expenseRepository.findById(id)
        .orElseThrow(() -> {
          logger.error("No expense found in the database with ID: {}", id);
          return new ExpenseNotFoundException();
        });
  }

  // Get all expenses
  @Override
  public List<Expense> getAllExpenses() {
    List<Expense> expenses = expenseRepository.findAll();
    logger.info("Fetched {} expenses from the database", expenses.size());
    return expenses;
  }

  // Edit expense
  @Transactional
  @Override
  public Expense editExpense(Long id, Expense newDetails) {
    // Fetch the existing expense
    Expense existingExpense = expenseRepository.findById(id)
        .orElseThrow(() -> {
          logger.error("No expense found in the database with ID: {}", id);
          return new ExpenseNotFoundException();
        });

    // Validate new details
    // Deliberately exclude sharedBy entity as some expenses might not be shared and
    // are paid entirely by a single person.
    if (newDetails.getType() == null || newDetails.getAmount() == null || newDetails.getPaidBy() == null) {
      logger.error("Invalid expense details provided for update");
      throw new IllegalArgumentException("Expense details cannot be null");
    }

    // Update the fields
    existingExpense.setType(newDetails.getType());
    existingExpense.setAmount(newDetails.getAmount());
    existingExpense.setDescription(newDetails.getDescription());
    existingExpense.setPaidBy(newDetails.getPaidBy());
    existingExpense.setSharedBy(newDetails.getSharedBy());

    logger.info("Successfully updated expense with ID: {}", id);
    return expenseRepository.save(existingExpense);
  }

  // Delete expense
  @Transactional
  @Override
  public void deleteExpense(Long id) {
    // Ensure the expense exists before deletion
    Expense expense = expenseRepository.findById(id)
        .orElseThrow(() -> {
          logger.error("No expense found in the database with ID: {}", id);
          return new ExpenseNotFoundException();
        });

    logger.info("Attempting to delete expense with ID: {}", id);

    // Delete the expense
    logger.info("Successfully deleted expense with ID: {}", id);
    expenseRepository.delete(expense);
  }

}

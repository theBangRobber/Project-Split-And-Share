package sg.edu.ntu.split_and_share.exception;

public class ExpenseNotFoundException extends RuntimeException {
  public ExpenseNotFoundException() {
    super("Expense not found.");
  }
}

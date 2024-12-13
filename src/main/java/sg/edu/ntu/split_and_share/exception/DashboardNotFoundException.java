package sg.edu.ntu.split_and_share.exception;

public class DashboardNotFoundException extends RuntimeException {
  public DashboardNotFoundException() {
    super("Dashboard not found.");
  }
}

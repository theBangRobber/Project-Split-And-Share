package sg.edu.ntu.split_and_share.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.User;

import sg.edu.ntu.split_and_share.exception.DashboardNotFoundException;
import sg.edu.ntu.split_and_share.exception.InvalidCredentialsException;
import sg.edu.ntu.split_and_share.exception.UserNotFoundException;
import sg.edu.ntu.split_and_share.exception.UsernameIsTakenException;
import sg.edu.ntu.split_and_share.repository.UserRepository;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
/*
 * @Transactional ensure that operations involving multiple database changes are
 * atomic. It is needed as creating/deleting a user will also create/delete the
 * affiliated dashboard
 */
public class UserServiceImpl implements UserService {

  private UserRepository userRepository;

  // Adding Password Encoder 
  private   PasswordEncoder passwordEncoder;


  private DashboardRepository dashboardRepository;
  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  // Constructor injection
  public UserServiceImpl(UserRepository userRepository, DashboardRepository dashboardRepository) {
    this.userRepository = userRepository;
    this.dashboardRepository = dashboardRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  // Create user and initialize a new dashboard
  @Override
  public User createUser(User user) {
    logger.info("Attempting to create user with username: {}", user.getUsername());
    // Check if the username already exists
    if (userRepository.findByUsername(user.getUsername()).isPresent()) {
      logger.error("Username {} is already taken", user.getUsername());
      throw new UsernameIsTakenException();
    }
    // Encrypt the password before saving in the db
    String encodedPassword = this.passwordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);
    logger.info("User {} registered successfully", user.getUsername());
    // return userRepository.save(user);

    // Initialize the dashboard and set up the relationship
    Dashboard dashboard = new Dashboard();
    dashboard.setName(user.getName() + "'s Dashboard");
    logger.info("Dashboard '{}' initialized for user '{}'", dashboard.getName(), user.getName());

    // Set the user in the dashboard and associate the dashboard with the user
    dashboard.setUser(user); // This establishes the relationship from Dashboard to User
    user.setDashboard(dashboard); // This sets the dashboard in the User entity

    // Save user (cascade will save dashboard automatically)
    logger.info("Saving user and initializing dashboard");
    return userRepository.save(user);
  }

  // Get user by username
  @Override
  public User getUser(String username) {
    logger.info("Fetching user with username: {}", username);

    return userRepository.findByUsername(username)
        .orElseThrow(() -> {
          logger.error("User with username '{}' not found.", username);
          return new UserNotFoundException();
        });
  }

  // Update user
  @Override
  public User updateUser(String username, User user) {
    logger.info("Attempting to update user with username: {}", username);
    User userToUpdate = userRepository.findByUsername(username).orElseThrow(() -> {
      logger.error("User with username '{}' not found for update.", username);
      return new UserNotFoundException();
    });

    userToUpdate.setName(user.getName());
    userToUpdate.setUsername(user.getUsername());
    String encodedPassword = this.passwordEncoder.encode(user.getPassword());
    userToUpdate.setPassword(encodedPassword);

    // Fetch associated dashboard using the user object (not the old username)
    Dashboard dashboard = userToUpdate.getDashboard(); // This directly gets the dashboard associated with the user

    if (dashboard == null) {
      logger.warn("No dashboard found for user '{}'. Cannot update user without dashboard.", username);
      throw new DashboardNotFoundException(); // You can throw this exception if a dashboard doesn't exist
    }

    // Update dashboard details
    dashboard.setName(user.getName()); // Set the new name from the User

    logger.info("User with username '{}' and associated dashboard updated successfully.", username);

    // Save both the updated User and Dashboard entities
    userRepository.save(userToUpdate);
    dashboardRepository.save(dashboard);

    return userToUpdate;
  }

  // Delete user
  @Override
  public void deleteUser(String username) {
    logger.info("Attempting to delete user with username: {}", username);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          logger.error("User with username '{}' not found for deletion.", username);
          return new UserNotFoundException();
        });

    logger.info("User with username '{}' deleted successfully along with associated dashboard.", username);
    userRepository.delete(user); // Automatically deletes the associated Dashboard
  }

  // Authenticate user credentials
  // public User authenticateUser(String username, String password) {
    // logger.info("Attempting to authenticate user with username: {}", username);

    // logger.info("User with username '{}' authenticated successfully.", username);
    // return userRepository.findByUsernameAndPassword(username, password)
        // .orElseThrow(() -> {
          // logger.warn("Authentication failed for username: '{}'", username);
          // return new InvalidCredentialsException("Authentication failed for username: '{}'");
        // });
  // }
  // Authenticate user credentials and displays the user after authentication successful
  public User authenticateUser(String username, String password) {
    logger.info("Attempting to authenticate user with username: {}", username);
    logger.info("User with username '{}' authenticated successfully.", username);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          logger.warn("Authentication failed for username: '{}' Username not valid,check username", username);
          return new UserNotFoundException();
        });
        if (!passwordEncoder.matches(password, user.getPassword())) {
          logger.warn("Invalid password for username: '{}'", username);
          throw new InvalidCredentialsException("Invalid password for username: '" + username + "'");
      }
      logger.info("User with username '{}' authenticated successfully.", username);
      return user;
}

  @Override
  
    public List<User> getAllUsers() {
      return userRepository.findAll();
    }
  

}

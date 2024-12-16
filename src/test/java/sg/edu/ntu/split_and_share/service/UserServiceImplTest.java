package sg.edu.ntu.split_and_share.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.User;
import sg.edu.ntu.split_and_share.exception.DashboardNotFoundException;
import sg.edu.ntu.split_and_share.exception.InvalidCredentialsException;
import sg.edu.ntu.split_and_share.exception.UserNotFoundException;
import sg.edu.ntu.split_and_share.exception.UsernameIsTakenException;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;
import sg.edu.ntu.split_and_share.repository.UserRepository;


@ExtendWith(MockitoExtension.class) // this extension automatically initializes @mock and @injectmocks annotations
public class UserServiceImplTest {

    // As our data structure is storing information in repo and inject in service
    // file. Hence need to use mock and inject.

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test // Create User Test for Success Creation - Checked correct
    public void testCreateUser_Successful() {
        // Arrange
        User newUser = User.builder()
                            .username("Mmanyuu")
                            .password("Password")
                            .name("Manyu")
                            .build(); //Proceed to create new user. 

        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(Optional.empty()); //this is when findbyusername() is called, the username is not occupied.
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");

        //simlulating saving the new user and uses thenAnswer instead of thenReturn is to have more flexibility in determining how the method should response if called.
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0); // Access the first argument of save() which is the entity User (everything within the entity)
            savedUser.setId(1L); // Setting the ID within the enity after access the entity from the code before this
            // savedUser.setPassword(passwordEncoder.encode(rawPassword));
            Dashboard dashboard = new Dashboard();
            dashboard.setName(savedUser.getName() + "'s Dashboard"); // Dynamically set dashboard name
            savedUser.setDashboard(dashboard); // Associate dashboard with user
            return savedUser; //return modified User
        });

        // Act
        User createdUser = userService.createUser(newUser);

        // Assert - checking individual fields of savedUser
        assertNotNull(createdUser, "Newly created user should not be null");
        assertNotNull(createdUser.getDashboard(), "Dashboard should be created and associated with the user");
        assertEquals(newUser.getUsername(), createdUser.getUsername(), "Username should match the input");
        assertEquals(newUser.getName(), createdUser.getName(), "Name should match the input");
        assertEquals(newUser.getDashboard().getName(), createdUser.getDashboard().getName(), "Dashboard name should match the user's name");

        // Verify
        verify(userRepository, times(1)).findByUsername(newUser.getUsername());
        verify(userRepository,times(1)).save(any(User.class));
        verify(passwordEncoder).encode(newUser.getPassword());
    }

    @Test
    public void testCreateUser_Unsucessful_UsernameTaken() {

        // Creating a user with username that is already taken and simulate the behavior
        // of findByUsername(). It will return a optional containing existing user
        User existingUser = User.builder().username("Mmanyuu").name("Manyu").build();
        when(userRepository.findByUsername("Mmanyuu")).thenReturn(Optional.of(existingUser));

        // Attempt to create another user with the same username
        User newUser = User.builder().username("Mmanyuu").name("NewUser").build();

        // Act & Assert - assert that the exception is suppose to be thrown when same username is detected
        Exception exception = assertThrows(UsernameIsTakenException.class, () -> userService.createUser(newUser),
                "Expected createUser to throw an exception, but it didn't");
        assertEquals("Username already exists", exception.getMessage());

        // Verify
        verify(userRepository, times(1)).findByUsername("Mmanyuu"); //Verify that the findByUsername is called only once
        verify(userRepository, never()).save(any(User.class)); //Verify that if a detection of same username, it will not be saved or user is not created
    }

    @Test
    public void testGetUser_Successful() {

        // Arrange - setup beginning information
        User user = User.builder().username("Mmanyuu").password("123456789").name("Manyu").build();
        when(userRepository.findByUsername("Mmanyuu")).thenReturn(Optional.of(user));

        //Act - execute the test method
        User foundUser = userService.getUser("Mmanyuu");

        //Assert - compare result
        assertNotNull(foundUser);
        assertEquals(user.getUsername(), foundUser.getUsername(),"Username input should be the same as in the data");
    }

    @Test
    public void testGetUser_Unsuccessful_UserNotFound() {
        //Mock repo behavior for non-existing username
        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        //Act and Assert
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.getUser("nonExistentUser"),
        "Expected getUser to throw an exception, but it didn't");
        assertEquals("User not found.", exception.getMessage()); //this should align with UserNotFoundException file

    }

    @Test
    public void testUpdateUser_Successful(){ //updateUser(String username, User user) - two parameters

        //Arrange - setting up the requirement. as this test is about successful update, hence all information should be registered and recorded

        User existingUser = User.builder()
                .id(1L)
                .username("Jane")
                .password("123456789")
                .name("Manyu")
                .dashboard(Dashboard.builder()
                    .name("Manyu")
                    .build())
                .build(); //Second parameter

        User userToUpdate = User.builder()
                .username("UpdatedUsername")
                .password("newPassword")
                .name("updatedName")
                .build();

        //Mock findbyusername().
        when(userRepository.findByUsername("Jane")).thenReturn(Optional.of(existingUser));

        //Save the user and return the updated user
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        //Mock passwordEncoder.encode()
        when(passwordEncoder.encode(userToUpdate.getPassword())).thenReturn("encryptedNewPassword");

        //simulate save behavior - dashboard repo. when an update of name, the dashboard name will also change
        when(dashboardRepository.save(any(Dashboard.class))).thenAnswer(invocation -> {
            Dashboard savedDashboard = invocation.getArgument(0);
            savedDashboard.setName(userToUpdate.getName());
            return savedDashboard;
        });

        //Act
        User updatedInfo = userService.updateUser("Jane", userToUpdate);

        //Assert
        assertNotNull(updatedInfo, "Updated info should not be Null");
        assertNotNull(updatedInfo.getDashboard(),"Dashboard should still be associated with the user");
        assertEquals(userToUpdate.getUsername(),updatedInfo.getUsername(),"Username should be updated - UpdatedUsername");
        assertEquals(userToUpdate.getName(), updatedInfo.getName(),"Name should be updated - updatedName");
        assertEquals(userToUpdate.getName(), updatedInfo.getDashboard().getName(), "Dashboard name should be updated");

        //Verify
        verify(userRepository,times(1)).findByUsername("Jane");
        verify(userRepository,times(1)).save(existingUser);
        verify(dashboardRepository,times(1)).save(existingUser.getDashboard());

        verify(passwordEncoder, times(1)).encode(userToUpdate.getPassword());
    }

    @Test
    public void testUpdateUser_Unsuccessful_DashboardNotFound(){
        //Arrange for an existing user with null dashboard
        User existingUser = User.builder().id(1L).username("Mmanyuu").password("123456789").name("Manyu").dashboard(null).build();

        User updatedUser = User.builder().username("UpdatedUsername").password("newPassword").name("updatedName").build();

        when(userRepository.findByUsername("Mmanyuu")).thenReturn(Optional.of(existingUser));

        Exception exception = assertThrows(DashboardNotFoundException.class, () -> userService.updateUser("Mmanyuu", updatedUser),"Expected updateUser to throw an exception, but it didn't");
        assertEquals("Dashboard not found.", exception.getMessage());

        verify(userRepository,never()).save(any(User.class)); //verify user repo save method not called
        verify(dashboardRepository, never()).save(any(Dashboard.class)); // verify dashboard repo save method not called
    }

    @Test
    public void testUpdateUser_Unsuccessful_UserNotFound(){

    String username = "nonExistentUser";
    User updatedUser = new User();

    //Mock repo behavior for non-existing username
    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    //Act and Assert
    Exception exception = assertThrows(UserNotFoundException.class, () -> userService.updateUser(username,updatedUser),"Expected updateUser to throw an exception, but it didn't");
    assertEquals("User not found.", exception.getMessage()); //this should align with UserNotFoundException file

    verify(userRepository,never()).save(any(User.class)); //verify user repo save method not called
    verify(dashboardRepository, never()).save(any(Dashboard.class)); // verify dashboard repo save method not called
    }

    @Test
    public void testDeleteUser_Successful(){
        String existingUsername = "Mmanyuu";

        User existingUser = User.builder()
            .id(1L)
            .username(existingUsername)
            .password("123456789")
            .name("Manyu")
            .dashboard(Dashboard.builder()
                .name("Manyu")
                .build())
            .build();

        when(userRepository.findByUsername(existingUsername)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(existingUsername);

        verify(userRepository, times(1)).findByUsername(existingUsername);
        verify(userRepository, times(1)).delete(existingUser); //ensure delete was called
    }

    @Test
    public void testDeleteUser_Unsuccessful_UserNotFound(){
        String username = "nonExistentUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //Act and Assert
        Exception exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(username),"Expected deleteUser to throw an exception, but it didn't");
        assertEquals("User not found.", exception.getMessage()); //this should align with UserNotFoundException file

        verify(userRepository,never()).save(any(User.class)); //verify user repo save method not called
        verify(dashboardRepository, never()).save(any(Dashboard.class)); // verify dashboard repo save method not called
    }

    @Test
    void testAuthenticateUser_Success() {
        // Arrange
        String username = "testUser";
        String rawPassword = "password123";

        //see Act for explaination
        BCryptPasswordEncoder realEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = realEncoder.encode(rawPassword);

        User mockUser = User.builder()
                .username(username)
                .password(encryptedPassword)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        when(passwordEncoder.matches(rawPassword, encryptedPassword)).thenReturn(true);

        System.out.println("Mocked password matches: " + passwordEncoder.matches(rawPassword, encryptedPassword));

        // Act - i got error at this point that state Encoded password does not look like BCrypt. When i search for information, i understand that when passwordEncoder.encode is called, it generate a unique hashed string
        //Hence, cannot directly compare the rawPassword with the encryptedPassword because the encryption process is non-deterministic. Every time encode is called, it produces a different hashed output (due to salting).
        User authenticatedUser = userService.authenticateUser(username, rawPassword);

        // Assert
        assertNotNull(authenticatedUser, "Authenticated user should not be null");
        assertEquals(username, authenticatedUser.getUsername(), "Username should match");
        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).matches(rawPassword, encryptedPassword);
    }
    @Test
    public void testAuthenticateUser_Unsuccessful_InvalidUsername (){

        when(userRepository.findByUsername("wrongUsername")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, ()-> userService.authenticateUser("wrongUsername", "Password"), "Expected authenticateUser to throw an exception, but it didn't");
        assertEquals("User not found.", exception.getMessage()); //this should align with UserNotFoundException file

        verify(userRepository,times(1)).findByUsername("wrongUsername");
    }
    
    @Test
    public void testAuthenticateUser_Unsuccessful_IncorrectPassword (){
        User mockUser = User.builder()
                .username("Mmanyuu")
                .password("Password")
                .name("Manyu")
                .build();

                when(userRepository.findByUsername("Mmanyuu")).thenReturn(Optional.of(mockUser));
                when(passwordEncoder.matches("incorrectPassword", mockUser.getPassword())).thenReturn(false);

       //Act and Assert
       assertThrows(InvalidCredentialsException.class, () -> userService.authenticateUser("Mmanyuu", "incorrectPassword"),"Expected authenticateUser to throw an exception, but it didn't");
    }
}


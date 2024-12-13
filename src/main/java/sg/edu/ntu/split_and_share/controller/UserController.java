package sg.edu.ntu.split_and_share.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sg.edu.ntu.split_and_share.entity.User;
import sg.edu.ntu.split_and_share.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

  private UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // Create new user
  // http://localhost:8080/api/user
  @PostMapping("")
  public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
    return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
  }

  // Get user by username
  // http://localhost:8080/api/user/{username}
  @GetMapping("/{username}")
  public ResponseEntity<User> getUser(@PathVariable String username) {
    return new ResponseEntity<>(userService.getUser(username), HttpStatus.OK);
  }

  // Update user by username
  // http://localhost:8080/api/user/{username}
  @PutMapping("/{username}")
  public ResponseEntity<User> updateUser(@PathVariable String username, @Valid @RequestBody User user) {
    return new ResponseEntity<>(userService.updateUser(username, user), HttpStatus.OK);
  }

  // Delete user
  // http://localhost:8080/api/user/{username}
  @DeleteMapping("/{username}")
  public ResponseEntity<HttpStatus> deleteUser(@PathVariable String username) {
    userService.deleteUser(username);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  // Authenticate an user
  // http://localhost:8080/api/user/authenticate
  @PostMapping("/authenticate")
  public ResponseEntity<User> authenticateUser(@Valid @RequestBody User user) {
    return new ResponseEntity<>(userService.authenticateUser(user.getUsername(), user.getPassword()), HttpStatus.OK);
  }
}
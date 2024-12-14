package sg.edu.ntu.split_and_share.service;

import java.util.List;

import sg.edu.ntu.split_and_share.entity.User;

public interface UserService {
  User createUser(User user);

  List<User> getAllUsers();

  User getUser(String username);

  User updateUser(String username, User user);

  void deleteUser(String name);

  User authenticateUser(String username, String password);

}

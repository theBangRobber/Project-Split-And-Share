package sg.edu.ntu.split_and_share.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
// https://howtodoinjava.com/spring-boot2/testing/spring-boot-mockmvc-example/

import sg.edu.ntu.split_and_share.entity.User;
import sg.edu.ntu.split_and_share.service.UserService;
// import sg.edu.ntu.split_and_share.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	private UserService userService; // Mocking the service layer

	@Test
	void shouldCreateUser() throws Exception {
		// // Create User with POST request
		User user = new User(null, "jane_doe", "mypassword123", "Jane Joe", null);

		// Mock the user service to return the user when createUser is called
		// given(userService.createUser(any(User.class))).willReturn(user);

		// Perform the POST request to the /api/user endpoint
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))) // Serialize the user object to JSON
				.andExpect(status().isCreated()) // Expect a 201 status code
				.andExpect(jsonPath("$.username").value("jane_doe")); // Verify the name in the response
	}

	@Test
	public void shouldGetUserByUsername() throws Exception {

		// Create User with POST request
		User user = new User(null, "jane_doe", "mypassword123", "Jane Joe", null);

		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))) // Serialize the user object to JSON
				.andExpect(status().isCreated()) // Expect a 201 status code
				.andExpect(jsonPath("$.username").value("jane_doe")); // Verify the name in the response

		when(userService.getUser("jane_doe")).thenReturn(user);

		// Act & Assert
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/user/jane_doe"))
				.andExpect(status().isOk()) // HTTP 200 OK
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Expect JSON response
				.andExpect(jsonPath("$.username").value("jane_doe")) // Check username field
				.andExpect(jsonPath("$.name").value("Jane Joe")); // Check fullName field
	}

	@Test
	public void shouldUpdateUser() throws Exception {
		// Arrange: Create a user object
		User user = new User(null, "jane_doe", "mypassword123", "Jane Joe", null);
		User updatedUser = new User(null, "jane_doe", "mypassword123", "Kelly Joe", null); // Updated user

		// Step 1: Create the user via POST request
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/user") // Assuming the user creation endpoint is /api/user
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(user))) // Serialize user object to JSON
				.andExpect(status().isCreated()) // Expect 201 (Created)
				.andExpect(jsonPath("$.username").value("jane_doe")); // Verify the username in the response

		// Step 2: Mock the service to return the updated user
		when(userService.getUser("jane_doe")).thenReturn(updatedUser);

		// Step 3: Update the user using PUT request and validate the response
		mockMvc.perform(MockMvcRequestBuilders
				.put("/api/user/jane_doe") // Correct the path for updating the user
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUser))) // Send updated user info
				.andExpect(status().isOk()) // Expect 200 (OK)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Expect JSON response
				.andExpect(jsonPath("$.name").value("Kelly Joe")); // Check that the full name is updated to "Jane Doe"
	}

	// @Test
	// public void testDeleteUser() throws Exception {
	// // Step 1: Build a GET request to /customers/1
	// RequestBuilder request = MockMvcRequestBuilders.get("/users/1");

	// // Step 2: Perform the request, get response and assert
	// mockMvc.perform(request)
	// // Assert that the status code is 200 OK
	// .andExpect(status().isOk())
	// // Assert that the content type is JSON
	// .andExpect(content().contentType(MediaType.APPLICATION_JSON))
	// // Assert that the id returned is 1
	// .andExpect(jsonPath("$.id").value(1));
	// }

	// @Test
	// public void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
	// // Arrange
	// when(userService.getUser("non_existing_user")).thenThrow(new
	// UserNotFoundException());

	// // Act & Assert
	// mockMvc.perform(MockMvcRequestBuilders
	// .get("/api/user/non_existing_user"))
	// .andExpect(status().isNotFound()) // HTTP 404 Not Found
	// .andExpect(jsonPath("$.error").value("User not found")); // Assuming an error
	// message in the response
	// }

}

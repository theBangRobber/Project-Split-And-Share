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

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	// Mocking the service layer
	private UserService userService;

	@Test
	void shouldCreateUser() throws Exception {
		// Create User with POST request
		User user = new User(null, "jane_doe", "mypassword123", "Jane Joe", null);

		// Mock the user service to return the user when createUser is called
		// given(userService.createUser(any(User.class))).willReturn(user);

		// Perform the POST request to the /api/user endpoint
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				// Serialize the user object to JSON
				.content(objectMapper.writeValueAsString(user)))
				// Expect a 201 status code
				.andExpect(status().isCreated())
				// Verify the name in the response
				.andExpect(jsonPath("$.username").value("jane_doe"));
	}

	@Test
	public void shouldGetUserByUsername() throws Exception {

		// Create User with POST request
		User user = new User(null, "jane_doe", "mypassword123", "Jane Joe", null);

		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				// Serialize the user object to JSON
				.content(objectMapper.writeValueAsString(user)))
				// Expect a 201 status code
				.andExpect(status().isCreated())
				// Verify the name in the response
				.andExpect(jsonPath("$.username").value("jane_doe"));

		when(userService.getUser("jane_doe")).thenReturn(user);

		// Act & Assert
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/user/jane_doe"))
				// HTTP 200 OK
				.andExpect(status().isOk())
				// Expect JSON response
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				// Check username field
				.andExpect(jsonPath("$.username").value("jane_doe"))
				// Check name field
				.andExpect(jsonPath("$.name").value("Jane Joe"));
	}

	@Test
	public void shouldUpdateUser() throws Exception {
		// Arrange: Create a user object
		User user = new User(null, "jane_doe", "mypassword123", "Jane Joe", null);
		// Updated user
		User updatedUser = new User(null, "jane_doe", "mypassword123", "Kelly Joe", null);

		// Step 1: Create the user via POST request
		mockMvc.perform(MockMvcRequestBuilders
				// Assuming the user creation endpoint is /api/user
				.post("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				// Serialize user object to JSON
				.content(objectMapper.writeValueAsString(user)))
				// Expect 201 (Created)
				.andExpect(status().isCreated())
				// Verify the username in the response
				.andExpect(jsonPath("$.username").value("jane_doe"));

		// Step 2: Mock the service to return the updated user
		when(userService.getUser("jane_doe")).thenReturn(updatedUser);

		// Step 3: Update the user using PUT request and validate the response
		mockMvc.perform(MockMvcRequestBuilders
				// Correct the path for updating the user
				.put("/api/user/jane_doe")
				.contentType(MediaType.APPLICATION_JSON)
				// Send updated user info
				.content(objectMapper.writeValueAsString(updatedUser)))
				// Expect 200 (OK)
				.andExpect(status().isOk())
				// Expect JSON response
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				// Check that the full name is updated to "Jane Doe"
				.andExpect(jsonPath("$.name").value("Kelly Joe"));
	}

	@Test
	public void testDeleteUser() throws Exception {

		// Step 1: Create a User with POST request
		User user = new User(null, "jane_doe", "mypassword123", "Jane Joe", null);

		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/user")
				.contentType(MediaType.APPLICATION_JSON)
				// Serialize the user object to JSON
				.content(objectMapper.writeValueAsString(user)))
				// Expect a 201 status code
				.andExpect(status().isCreated())
				// Verify the name in the response
				.andExpect(jsonPath("$.username").value("jane_doe"));

		// Step 2: Mock the service method to return the user
		when(userService.getUser("jane_doe")).thenReturn(user);

		// Step 3: Send DELETE request
		mockMvc.perform(MockMvcRequestBuilders
				// Send a DELETE request with the username path variable
				.delete("/api/user/{username}", "jane_doe"))
				// Expect a 204 status code (No Content), meaning successful deletion
				.andExpect(status().isNoContent());

		// Step 4: Verify that the user was deleted by mocking the service to return
		// null after deletion
		when(userService.getUser("jane_doe")).thenReturn(null);

		// Optionally, you could check that the user does not exist anymore
		mockMvc.perform(MockMvcRequestBuilders
				.get("/api/user/{username}", "jane_doe"))
				// Expect a 404 status code, meaning the user is not found
				.andExpect(status().isNotFound());
	}

}

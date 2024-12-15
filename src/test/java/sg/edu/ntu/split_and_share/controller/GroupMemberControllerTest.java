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

import java.util.Arrays;
import java.util.List;
import sg.edu.ntu.split_and_share.entity.User;
import sg.edu.ntu.split_and_share.repository.GroupMemberRepository;
import sg.edu.ntu.split_and_share.service.UserService;
// import sg.edu.ntu.split_and_share.entity.GroupMember;

@SpringBootTest
@AutoConfigureMockMvc
public class GroupMemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	// Mocking the service layer
	private UserService userService;

	@Mock
	private GroupMemberRepository GroupMemberRepository;

	// Add group member(s) to user's dashboard
	// http://localhost:8080/api/group-members/add/{username}

	// Remove group member(s) from user's dashboard
	// http://localhost:8080/api/group-members/remove/{username}/{memberName}

	// Get all group members
	// http://localhost:8080/api/group-members/list/{username}

	@Test
	void shouldAddGroupMembers() throws Exception {
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

		// Assuming group member list is represented as a list of usernames
		List<String> groupMembers = Arrays.asList("jane_doe", "may", "june", "july", "august");

		// Perform POST request to add group members
		mockMvc.perform(MockMvcRequestBuilders
				.post("/api/group-members/add/jane_doe")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(groupMembers)))
				.andExpect(status().isCreated());
	}

}
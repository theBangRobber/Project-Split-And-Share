package sg.edu.ntu.split_and_share.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
// import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.ntu.split_and_share.entity.GroupMember;
import sg.edu.ntu.split_and_share.repository.GroupMemberRepository;

@WebMvcTest(GroupMemberController.class)
public class GroupMemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Mock
	private GroupMemberRepository GroupMemberRepository;

	// Add code here

	// Add group member(s) to user's dashboard
	// http://localhost:8080/api/group-members/add/{username}

	// Remove group member(s) from user's dashboard
	// http://localhost:8080/api/group-members/remove/{username}/{memberName}

	// Get all group members
	// http://localhost:8080/api/group-members/list/{username}

	// @Test
	// public void testGetGroupMemberById() throws Exception {
	// 	// Step 1: Build a GET request to /customers/1
	// 	RequestBuilder request = MockMvcRequestBuilders.get("/groupmember/1");

	// 	// Step 2: Perform the request, get response and assert
	// 	mockMvc.perform(request)
	// 			// Assert that the status code is 200 OK
	// 			.andExpect(status().isOk())
	// 			// Assert that the content type is JSON
	// 			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
	// 			// Assert that the id returned is 1
	// 			.andExpect(jsonPath("$.id").value(1));
	// }

	// @Test
	// public void testInvalidGroupMemberId() throws Exception {
	// 	// Create customer with invalid fields
	// 	GroupMember invalidMember = new GroupMember();

	// 	// Convert object to JSON
	// 	String invalidMemberAsJson = objectMapper.writeValueAsString(invalidMember);

	// 	// Build the request
	// 	RequestBuilder request = MockMvcRequestBuilders.post("/customers")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(invalidMemberAsJson);

	// 	// Perform request
	// 	mockMvc.perform(request)
	// 			.andExpect(status().isBadRequest())
	// 			.andExpect(content().contentType(MediaType.APPLICATION_JSON));

	// }

}

package sg.edu.ntu.split_and_share.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.entity.User;
import sg.edu.ntu.split_and_share.service.DashboardService;
import sg.edu.ntu.split_and_share.service.ExpenseService;
import sg.edu.ntu.split_and_share.service.UserService;

// @SpringBootTest
// @AutoConfigureMockMvc
@WebMvcTest(DashboardController.class)
public class DashboardControllerTest {
	// Get total sum of all expenses
	// http://localhost:8080/api/dashboard/{username}/total-sum

	// Get total sum of each expense type
	// http://localhost:8080/api/dashboard/{username}/sum-by-type

	// Count the number of each expense type
	// http://localhost:8080/api/dashboard/{username}/count-by-type

	// Count the grand total number of expenses
	// http://localhost:8080/api/dashboard/{username}/count-total

	// Calculate individual/group member balances
	// http://localhost:8080/api/dashboard/{username}/balances

	// Settle balance among group members
	// http://localhost:8080/api/dashboard/{username}settlement

	// Fetch all expenses details
	// http://localhost:8080/api/dashboard/{username}/expenses

	// Reset dashboard
	// http://localhost:8080/api/dashboard/{username}/reset

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@MockBean
	private ExpenseService expenseService;

	@MockBean
	private DashboardService dashboardService;

	// http://localhost:8080/api/dashboard/{username}/total-sum
	@Test
	public void shouldTotalSum() throws Exception {
		// Arrange - Prepare the Dashboard object and mock services
		Dashboard dashboard = new Dashboard(1L, null, "John", null, null);
		String dashboardJson = objectMapper.writeValueAsString(dashboard);
		double expectedTotalSum = 100.0; // Assuming 100 is the expected sum returned by the service

		// Mock the service method to return the expected value when calculateTotalSum
		// is called
		when(dashboardService.calculateTotalSum(eq("John"))).thenReturn(expectedTotalSum);

		// Act - Perform the POST request
		mockMvc.perform(post("/api/dashboard/john/total-sum")
				.contentType(MediaType.APPLICATION_JSON)
				.content(dashboardJson))
				.andExpect(status().isCreated()) // Ensure that the response status is 201 Created
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Check the content type of the response
				.andExpect(jsonPath("$.totalSum").value(expectedTotalSum)); // Check if the expected value is returned
																			// in the response

		// Assert - Verify interactions with the mocked service
		verify(dashboardService, times(1)).calculateTotalSum(eq("john")); // Ensure the service was called exactly once
																			// with the correct username
	}

	// http://localhost:8080/api/dashboard/{username}/total-sum
	// @Test
	// public void shouldTotalSum() throws Exception {
	// // Create User object for the test
	// User user = new User(null, "john", "mypassword123", "John", null);

	// // Perform POST request to create a new user
	// mockMvc.perform(MockMvcRequestBuilders
	// .post("/api/user")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content(objectMapper.writeValueAsString(user)))
	// .andExpect(status().isCreated());

	// // Assuming group member list is represented as a list of usernames
	// List<String> groupMembers = Arrays.asList("john", "may", "june", "july",
	// "august");

	// // Perform POST request to add group members
	// mockMvc.perform(MockMvcRequestBuilders
	// .post("/api/group-members/add/john")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content(objectMapper.writeValueAsString(groupMembers)))
	// .andExpect(status().isCreated());

	// // Prepare expense object
	// Expense expense = new Expense(1L, "Food", 10.0, "Dinner", "John", new
	// Dashboard(), Set.of());
	// String expenseJson = objectMapper.writeValueAsString(expense);
	// // Mock expenseService to return created expense
	// when(expenseService.addExpense(any(Expense.class),
	// eq("john"))).thenReturn(expense);

	// // Perform POST to add expense
	// mockMvc.perform(MockMvcRequestBuilders
	// .post("/api/expense/john/add")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content(expenseJson))
	// .andExpect(status().isCreated());

	// // Mock dashboardService to return total sum of 10.0
	// when(dashboardService.calculateTotalSum("john")).thenReturn(10.0);

	// // Perform GET to check total sum
	// mockMvc.perform(get("/api/dashboard/john/total-sum")
	// .contentType(MediaType.APPLICATION_JSON))
	// .andExpect(jsonPath("$.totalSum").value(10.0))
	// .andExpect(status().isOk());

	// // Verify dashboardService method call
	// verify(dashboardService, times(1)).calculateTotalSum("john");
	// }

	// // http://localhost:8080/api/dashboard/{username}/sum-by-type
	// @Test
	// public void shouldGetSumByType() throws Exception {
	// // Create User object for the test
	// User user = new User(null, "john", "mypassword123", "John", null);

	// // Perform POST request to create a new user
	// mockMvc.perform(MockMvcRequestBuilders
	// .post("/api/user")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content(objectMapper.writeValueAsString(user)))
	// .andExpect(status().isCreated());

	// // Assuming group member list is represented as a list of usernames
	// List<String> groupMembers = Arrays.asList("john", "may", "june", "july",
	// "august");

	// // Perform POST request to add group members
	// mockMvc.perform(MockMvcRequestBuilders
	// .post("/api/group-members/add/john")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content(objectMapper.writeValueAsString(groupMembers)))
	// .andExpect(status().isCreated());

	// // Prepare expense object
	// Expense expense = new Expense(1L, "Food", 10.0, "Dinner", "John", new
	// Dashboard(), Set.of());
	// String expenseJson = objectMapper.writeValueAsString(expense);

	// // Mock expenseService to return created expense
	// when(expenseService.addExpense(any(Expense.class),
	// eq("john"))).thenReturn(expense);

	// // Perform POST to add expense
	// mockMvc.perform(MockMvcRequestBuilders
	// .post("/api/expense/john/add")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content(expenseJson))
	// .andExpect(status().isCreated());

	// // Prepare mock for calculating total sum by type (sum of expenses by type)
	// Map<String, Double> expensesByType = new HashMap<>();
	// expensesByType.put("Food", 10.0); // Add a mock sum for the "Food" type

	// // Mock dashboardService to return sum by type
	// when(dashboardService.sumExpensesByType("john")).thenReturn(expensesByType);

	// // Perform GET to check total sum by type
	// mockMvc.perform(MockMvcRequestBuilders
	// .get("/api/dashboard/john/sum-by-type")
	// .contentType(MediaType.APPLICATION_JSON))
	// .andExpect(status().isOk())
	// .andExpect(jsonPath("$.Food").value(10.0)); // Assuming "Food" is the key and
	// 10.0 is the value

	// // Verify dashboardService method call
	// verify(dashboardService, times(1)).sumExpensesByType("john");
	// }

	// // http://localhost:8080/api/dashboard/{username}/count-by-type
	// @Test
	// public void shouldGetCountByType() throws Exception {
	// // Create User object for the test
	// User user = new User(null, "john", "mypassword123", "John", null);

	// // Perform POST request to create a new user
	// mockMvc.perform(MockMvcRequestBuilders
	// .post("/api/user")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content(objectMapper.writeValueAsString(user)))
	// .andExpect(status().isCreated());

	// // Assuming group member list is represented as a list of usernames
	// List<String> groupMembers = Arrays.asList("john", "may", "june", "july",
	// "august");

	// // Perform POST request to add group members
	// mockMvc.perform(MockMvcRequestBuilders
	// .post("/api/group-members/add/john")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content(objectMapper.writeValueAsString(groupMembers)))
	// .andExpect(status().isCreated());

	// // Prepare expense object
	// Expense expense = new Expense(1L, "Food", 10.0, "Dinner", "John", new
	// Dashboard(), Set.of());
	// String expenseJson = objectMapper.writeValueAsString(expense);

	// // Mock expenseService to return created expense
	// when(expenseService.addExpense(any(Expense.class),
	// eq("john"))).thenReturn(expense);

	// // Perform POST to add expense
	// mockMvc.perform(MockMvcRequestBuilders
	// .post("/api/expense/john/add")
	// .contentType(MediaType.APPLICATION_JSON)
	// .content(expenseJson))
	// .andExpect(status().isCreated());

	// // Prepare mock for calculating total sum by type (sum of expenses by type)
	// Map<String, Double> expensesByType = new HashMap<>();
	// expensesByType.put("Food", 10.0); // Add a mock sum for the "Food" type

	// // Mock dashboardService to return sum by type
	// when(dashboardService.sumExpensesByType("john")).thenReturn(expensesByType);

	// // Perform GET to check total sum by type
	// mockMvc.perform(MockMvcRequestBuilders
	// .get("/api/dashboard/john/count-by-type")
	// .contentType(MediaType.APPLICATION_JSON))
	// .andExpect(status().isOk())
	// .andExpect(jsonPath("$.Food").value(10.0)); // Assuming "Food" is the key and
	// 10.0 is the value

	// // Verify dashboardService method call
	// verify(dashboardService, times(1)).sumExpensesByType("john");
	// }

}

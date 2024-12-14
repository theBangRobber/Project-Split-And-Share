package sg.edu.ntu.split_and_share.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.exception.ExpenseNotFoundException;
import sg.edu.ntu.split_and_share.service.ExpenseService;

//Setsup spring MVC env for testing the ExpenseController class
@WebMvcTest(ExpenseController.class)
public class ExpenseControllerTest {
    // testing workflow: (S)etup ,(E)xecute, (A)ssertion
    // Mock the service layer(S)
    // Perform the request(E)
    // Assert the response(A)
    // Verify the reponse with the expected result
    // -----------------------------------------------------------------
    // 5- Endpoints to be tested and 1-Exception Handling
    // 1) Create a new expense
// http://localhost:8080/api/expense/{username}/add
// 2) Get an expense by ID
// http://localhost:8080/api/expense/{id}
// 3)// Get all expenses
// http://localhost:8080/api/expense
// 4)Edit an expense
// http://localhost:8080/api/expense/{id}
// 5)Delete an expense
// http://localhost:8080/api/expense/{id}
// 6)Exception-ExpenseNotFoundException



    @Autowired
    private MockMvc mockMvc;// MockMvc is used to perform the HTTP request and validate the response

    @Autowired
    private ObjectMapper objectMapper;// ObjectMapper is used to convert java objects to JSON strings for HTTP requests
         
    // MockeBean is used to simulate the behaviour and isolate the controller for testing.Here we are injecting the ExpenseService class
    @MockBean
    private ExpenseService expenseService;
                                          
    // Test for the endpoint Adding a new expense : POST /api/expense/{username}/add
    @Test
    public void testAddExpenseSuccessfully() throws Exception {
        Expense expense = new Expense(1L, "Food", 10.0, "Dinner", "John", new Dashboard(), Set.of());
        String expenseJson = objectMapper.writeValueAsString(expense);
        // Step 1: Mock the data
        when(expenseService.addExpense(any(Expense.class), eq("john"))).thenReturn(expense);
        // Step 2 : Build a get Request and  Perform the request
                mockMvc.perform(post("/api/expense/john/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(expenseJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("Food"))
                .andExpect(jsonPath("$.amount").value(10.0))
                .andExpect(jsonPath("$.description").value("Dinner"))
                .andExpect(jsonPath("$.paidBy").value("John"));
        // Step 3: verify the response to the intented response
        // Pass the test if its valid
        verify(expenseService, times(1)).addExpense(any(Expense.class), eq("john"));
        // verify(expenseService, times(1)) - instructs mockito to verify interactions with expenseService,checks if the addexpense() is called once
        // and checks the value added(username) is matching exactly 
    }

    @Test
    public void testReturnExpenseById() throws Exception {
        Expense expense = new Expense(1L, "Groceries", 50.0, "Monthly shopping", "Alice", new Dashboard(), Set.of());
        when(expenseService.getExpense(1L)).thenReturn(expense);

        mockMvc.perform(get("/api/expense/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("Groceries"))
                .andExpect(jsonPath("$.amount").value(50.0))
                .andExpect(jsonPath("$.description").value("Monthly shopping"))
                .andExpect(jsonPath("$.paidBy").value("Alice"));

        verify(expenseService, times(1)).getExpense(1L);
    }

    @Test
    public void testReturnAllExpenses() throws Exception {
        List<Expense> expenses = Arrays.asList(
                new Expense(1L, "Transport", 15.0, "Bus fare", "John", new Dashboard(), Set.of()),
                new Expense(2L, "Entertainment", 30.0, "Movie night", "Doe", new Dashboard(), Set.of()));
        when(expenseService.getAllExpenses()).thenReturn(expenses);

        mockMvc.perform(get("/api/expense")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("Transport"))
                .andExpect(jsonPath("$[1].type").value("Entertainment"));

        verify(expenseService, times(1)).getAllExpenses();
    }

    @Test
    public void testUpdateExpenseSuccessfully() throws Exception {
        Expense updatedExpense = new Expense(1L, "Food", 25.0, "Lunch", "John", new Dashboard(), Set.of());
        String updatedExpenseJson = objectMapper.writeValueAsString(updatedExpense);

        when(expenseService.updateExpense(eq(1L), any(Expense.class))).thenReturn(updatedExpense);

        mockMvc.perform(put("/api/expense/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedExpenseJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(25.0));

        verify(expenseService, times(1)).updateExpense(eq(1L), any(Expense.class));
    }

    @Test
    public void testDeleteExpenseSuccessfully() throws Exception {
        doNothing().when(expenseService).deleteExpense(1L);

        mockMvc.perform(delete("/api/expense/1"))
                .andExpect(status().isNoContent());

        verify(expenseService, times(1)).deleteExpense(1L);
    }

    @Test
    public void testHandleExpenseNotFound() throws Exception {
        // Simulating the expense id as 99 ,when there is no expense created it throws the ExpenseNotFound Exception
        // With the status message we assert its valid or not
        when(expenseService.getExpense(99L)).thenThrow(new ExpenseNotFoundException());

        mockMvc.perform(get("/api/expense/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Expense not found."));

        verify(expenseService, times(1)).getExpense(99L);
    }
}

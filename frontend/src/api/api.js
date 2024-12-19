import axios from "axios";

const API_BASE_URL = "http://localhost:8080/api";

const api = axios.create({
  baseURL: API_BASE_URL,
});

// USER //

// Create new user
export const createUser = async (userData) => {
  return await api.post("/user", userData);
};

// Authenticate an user
export const authenticateUser = async (credentials) => {
  return await api.post("/user/authenticate", credentials);
};

// Delete user
export const deleteUser = async (username) => {
  return await api.delete(`/user/${username}`);
};

// DASHBOARD //

// Get total sum of all expenses
export const getTotalSum = async (username) => {
  return await api.get(`/dashboard/${username}/total-sum`);
};

// Get total sum of each expense type
export const getSumByType = async (username) => {
  return await api.get(`/dashboard/${username}/sum-by-type`);
};

// Count the number of each expense type
export const getCountByType = async (username) => {
  return await api.get(`/dashboard/${username}/count-by-type`);
};

// Count the grand total number of expenses
export const getCountTotal = async (username) => {
  return await api.get(`/dashboard/${username}/count-total`);
};

// Calculate individual/group member balances
export const getBalances = async (username) => {
  return await api.get(`/dashboard/${username}/balances`);
};

// Settle balance among group members
export const settleBalances = async (username) => {
  return await api.get(`/dashboard/${username}/settlement`);
};

// Reset dashboard
export const resetDashboard = async (username) => {
  return await api.delete(`/dashboard/${username}/reset`);
};

// GROUP MEMBER //

// Add group member(s)
export const addGroupMembers = async (username, groupMemberList) => {
  return await api.post(
    `/group-members/add/${username}`,
    groupMemberList
  );
};

// Get all group members
export const getAllGroupMembers = async (username) => {
  return await api.get(`/group-members/list/${username}`);
};

// Delete group member
export const removeGroupMember = async (username, memberName) => {
  return await api.delete(
    `/group-members/remove/${username}/${memberName}`
  );
};

// EXPENSE //

// Create an expense
export const createExpense = async (username, expenseData) => {
  return await api.post(`/expense/${username}/add`, expenseData);
};

// Edit an expense
export const editExpense = async (id, updatedData) => {
  return await api.put(`/expense/${id}`, updatedData);
};

// Get all expenses
export const getAllExpenses = async () => {
  return await api.get("/expense");
};

// Delete an expense
export const deleteExpense = async (id) => {
  return await api.delete(`/expense/${id}`);
};

export default api;

// src/components/Dashboard.js
import React, { useEffect, useState } from "react";
import {
  getTotalSum,
  getSumByType,
  settleBalances,
} from "../api/api";
import "../styles/styles.css";

const Dashboard = ({ expenses, username, dashboardName }) => {
  const [groupedExpenses, setGroupedExpenses] = useState({});
  const [totalSum, setTotalSum] = useState(null);
  const [sumByType, setSumByType] = useState({});
  const [settlementResult, setSettlementResult] = useState(null);

  // Log to check if dashboardName is received correctly
  console.log("Received username in Dashboard:", username);
  console.log("Received dashboardName in Dashboard:", dashboardName);
  console.log("Received expenses in Dashboard:", expenses);

  useEffect(() => {
    groupExpensesByType(expenses);
  }, [expenses]);

  // Fetch totals
  useEffect(() => {
    if (!username) return;

    const fetchDashboardData = async () => {
      try {
        // Fetch total sum
        const totalSumResponse = await getTotalSum(username);
        setTotalSum(totalSumResponse.data);
        console.log("Total sum fetched:", totalSumResponse.data); // Debugging log

        // Fetch sum by type
        const sumByTypeResponse = await getSumByType(username);
        setSumByType(sumByTypeResponse.data);
        console.log("Sum by type fetched:", sumByTypeResponse.data); // Debugging log
      } catch (error) {
        console.error("Error fetching dashboard data:", error);
      }
    };

    fetchDashboardData();
  }, [username, expenses]); // Dependencies include username and expenses

  // Color mapping for different expense types
  const expenseTypeColors = {
    Food: "#A33757",
    Transport: "#DC586D",
    Entertainment: "#9C82A3",
    Shopping: "#662249",
    Accommodation: "#F06C9B",
    Misc: "#FF6B6B",
  };

  // Group expenses by type for better organization
  const groupExpensesByType = (expenses) => {
    const grouped = expenses.reduce((acc, expense) => {
      if (!acc[expense.type]) {
        acc[expense.type] = [];
      }
      acc[expense.type].push(expense);
      return acc;
    }, {});
    setGroupedExpenses(grouped);
  };

  // Handle settlement and format response
  const handleSettlement = async () => {
    if (!username) {
      console.error("Username is required for settlement.");
      return;
    }

    try {
      const response = await settleBalances(username);
      const jsonResult = response.data;
      console.log("Raw settlement result:", jsonResult);

      // Convert JSON result to plain English format
      const plainEnglishResult = Object.entries(jsonResult)
        .map(([payer, transactions]) =>
          transactions
            .map((transaction) => `${payer} ${transaction}`)
            .join("\n")
        )
        .join("\n");

      setSettlementResult(plainEnglishResult); // Update state with plain English text
    } catch (error) {
      console.error("Error settling balances:", error);
      setSettlementResult(
        "Error settling balances. Please try again."
      );
    }
  };

  return (
    <div className="dashboard">
      {/* Dashboard Header */}
      <div className="dashboard-heading-container">
        <h1 className="dashboard-name">
          {dashboardName
            ? `${dashboardName} Expenses @ a glance`
            : "Your Expenses @ a glance"}
        </h1>
        {/* Fallback to "Your Dashboard" */}
        <button
          type="button"
          className="settlement-button"
          onClick={handleSettlement}
        >
          Settlement
        </button>
      </div>

      {/* Dashboard Summary */}
      <div className="dashboard-summary-container">
        <div className="expenses-column">
          {/* Expenses by Type */}
          <div className="sum-by-type">
            {Object.keys(sumByType).length > 0 ? (
              Object.keys(sumByType).map((type) => (
                <div key={type}>
                  <p>
                    {type}: ${sumByType[type]}
                  </p>
                </div>
              ))
            ) : (
              <p className="loading">Loading...</p>
            )}
          </div>

          {/* Total Expenses */}
          {totalSum !== null && totalSum !== 0 && (
            <div className="total-sum">
              <h2 className="total-sum-h2">Total Expenses: </h2>
              <p className="total-sum-data">${totalSum}</p>
            </div>
          )}
        </div>

        {/* Settlement Result */}
        {settlementResult && (
          <div className="settlement-result">
            <h2 className="settlement-result-h2">
              Settlement Summary:
            </h2>
            {settlementResult.split("\n").map((line, index) => (
              <p key={index}>{line}</p> // Render each line separately
            ))}
          </div>
        )}
      </div>

      {/* Expense Itemized List */}
      <div className="expenses-container">
        {Object.keys(groupedExpenses).map((type) => (
          <div key={type} className="expense-type">
            <h2 className="expense-type-h2">{type}</h2>
            <div className="expense-list">
              {groupedExpenses[type].map((expense, index) => (
                <div
                  key={index}
                  className="expense-item"
                  style={{
                    backgroundColor:
                      expenseTypeColors[type] || "#FF6B6B", // Default color
                  }}
                >
                  <p className="expense-description">
                    {expense.description || "No description"}
                  </p>
                  <p className="expense-amount">
                    ${expense.amount || "0.00"}
                  </p>
                  <p className="expense-paid-by">
                    Paid by: {expense.paidBy || "Unknown"}
                  </p>
                  <p className="expense-shared-by">
                    Shared by:{" "}
                    {Array.isArray(expense.sharedBy)
                      ? expense.sharedBy
                          .map(
                            (member) => member.memberName || "Unknown"
                          )
                          .join(", ")
                      : "No one"}
                  </p>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Dashboard;

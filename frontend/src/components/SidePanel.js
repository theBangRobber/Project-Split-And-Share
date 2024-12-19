import React from "react";
import RegistrationForm from "./RegistrationForm";
import AddMemberForm from "./AddMemberForm";
import AddExpenseForm from "./AddExpenseForm";
import "../styles/styles.css";

const SidePanel = ({
  isRegistered,
  setIsRegistered,
  username,
  setUsername,
  setDashboardName, // Add this prop to pass to RegistrationForm
  members,
  setMembers,
  expenses,
  setExpenses,
  fetchExpenses,
}) => {
  const handleAddExpense = async (newExpense) => {
    setExpenses((prevExpenses) => [...expenses, newExpense]);
    try {
      await fetchExpenses();
    } catch (error) {
      console.error(
        "Error fetching expenses after adding a new one:",
        error
      );
    }
  };

  return (
    <div className="side-panel">
      <h2 className="side-panel-h2">
        {isRegistered ? `Hello, ${username}` : "Hello There"}
      </h2>
      {!isRegistered ? (
        <div className="registration-form">
          <RegistrationForm
            setIsRegistered={setIsRegistered}
            setUsername={setUsername}
            setDashboardName={setDashboardName} // Pass this to RegistrationForm
          />
        </div>
      ) : members.length === 0 ? (
        <div className="add-member-form">
          <AddMemberForm
            username={username}
            setMembers={setMembers}
          />
        </div>
      ) : (
        <div className="add-expense-form">
          <AddExpenseForm
            username={username}
            members={members}
            setExpenses={handleAddExpense}
          />
        </div>
      )}
    </div>
  );
};

export default SidePanel;

import React, { useEffect, useState } from "react";
import { createExpense } from "../api/api";
import "../styles/styles.css";

const AddExpenseForm = ({ username, members = [], setExpenses }) => {
  const [type, setType] = useState("");
  const [amount, setAmount] = useState("");
  const [description, setDescription] = useState("");
  const [paidBy, setPaidBy] = useState("");
  const [sharedBy, setSharedBy] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    console.log("Members passed to AddExpenseForm:", members);
  }, [members]);

  const handlePaidByChange = (e) => {
    const { value } = e.target;
    setPaidBy(value);
    console.log("PaidBy selected:", value);
  };

  const handleSharedByChange = (e) => {
    const { value, checked } = e.target;
    setSharedBy((prevSharedBy) =>
      checked
        ? [...prevSharedBy, value]
        : prevSharedBy.filter((member) => member !== value)
    );
    console.log("SharedBy updated:", sharedBy);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (
      type &&
      amount > 0 &&
      description &&
      paidBy &&
      sharedBy.length > 0
    ) {
      try {
        const expenseData = {
          type,
          amount: parseFloat(amount),
          description,
          paidBy,
          sharedBy: sharedBy.map((name) => ({ memberName: name })),
        };

        console.log(
          "Submitting transformed expenseData:",
          expenseData
        );

        const response = await createExpense(username, expenseData);

        setExpenses((prevExpenses) => [
          ...prevExpenses,
          response.data,
        ]);
        setType("");
        setAmount("");
        setDescription("");
        setPaidBy("");
        setSharedBy([]);
        setError("");
      } catch (err) {
        console.error("Error adding expense:", err);
        setError("Error adding expense. Please try again.");
      }
    } else {
      setError("Please fill all fields correctly.");
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="dynamic-form add-expense-form"
    >
      <h2 className="form-title">Add Expense</h2>
      {error && <p className="error-message">{error}</p>}

      <div className="form-group">
        <label htmlFor="type" className="form-label">
          Type
        </label>
        <select
          id="type"
          className="form-input form-drop-menu"
          value={type}
          onChange={(e) => setType(e.target.value)}
          required
        >
          <option value="" disabled>
            Select Expense Type
          </option>
          <option value="Food">Food</option>
          <option value="Transport">Transport</option>
          <option value="Shopping">Shopping</option>
          <option value="Entertainment">Entertainment</option>
          <option value="Accommodation">Accommodation</option>
          <option value="Misc">Misc</option>
        </select>
      </div>

      <div className="form-group">
        <label htmlFor="amount" className="form-label">
          Amount
        </label>
        <input
          type="number"
          id="amount"
          className="form-input"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          min="0.10"
          step="0.10"
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="description" className="form-label">
          Description
        </label>
        <input
          type="text"
          id="description"
          className="form-input"
          autoComplete="off"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          required
        />
      </div>

      <div className="form-group">
        <label className="form-label">Paid By</label>
        {members.length > 0 ? (
          members.map((member, index) => (
            <div key={index} className="form-radio">
              <input
                type="radio"
                name="paidBy"
                id={`paidBy-${index}`}
                value={member}
                onChange={(e) => handlePaidByChange(e)}
                checked={paidBy === member}
                required
              />
              <label htmlFor={`paidBy-${index}`}>{member}</label>
            </div>
          ))
        ) : (
          <p>No members available</p>
        )}
      </div>

      <div className="form-group">
        <label className="form-label">Shared By</label>
        {members.length > 0 ? (
          members.map((member, index) => (
            <div key={index} className="form-checkbox">
              <input
                type="checkbox"
                id={`sharedBy-${index}`}
                value={member}
                onChange={(e) => handleSharedByChange(e)}
                checked={sharedBy.includes(member)}
              />
              <label htmlFor={`sharedBy-${index}`}>{member}</label>
            </div>
          ))
        ) : (
          <p>No members available</p>
        )}
      </div>

      <button type="submit" className="form-button">
        Add Expense
      </button>
    </form>
  );
};

export default AddExpenseForm;

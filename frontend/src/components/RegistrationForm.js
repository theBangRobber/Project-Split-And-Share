import React, { useState } from "react";
import { createUser } from "../api/api";

const RegistrationForm = ({
  setIsRegistered,
  setUsername,
  setDashboardName,
}) => {
  // Add setUsername prop
  const [usernameInput, setUsernameInput] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [dashboardNameState, setDashboardNameState] = useState(""); // Separate state for dashboard name
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Log inputs for debugging
    console.log("Username Input:", usernameInput);
    console.log("Password:", password);
    console.log("Name:", name);
    console.log("Dashboard Name State:", dashboardNameState);
    if (usernameInput && password.length >= 8 && name) {
      try {
        await createUser({ username: usernameInput, password, name });
        setIsRegistered(true);
        setUsername(usernameInput); // Set the username after registration

        // Determine the dashboard name (use default or the one the user inputs)
        const dashboardNameToUse = dashboardNameState;

        // Log the dashboard name for debugging
        console.log("Setting Dashboard Name:", dashboardNameToUse);

        setDashboardName(dashboardNameToUse); // Set the dashboard name
      } catch (err) {
        setError("Error during registration. Please try again.");
      }
    } else {
      setError("Please fill all fields correctly.");
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="dynamic-form registration-form"
    >
      <h2 className="form-title">Registration</h2>
      {error && <p className="error-message">{error}</p>}
      <div className="form-group">
        <label htmlFor="username" className="form-label">
          Username
        </label>
        <input
          type="text"
          id="username"
          className="form-input"
          autoComplete="off"
          value={usernameInput}
          onChange={(e) => setUsernameInput(e.target.value)}
          required
        />
      </div>
      <div className="form-group">
        <label htmlFor="password" className="form-label">
          Password
        </label>
        <input
          type="password"
          id="password"
          className="form-input"
          autoComplete="new-password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          minLength={8}
          required
        />
      </div>
      <div className="form-group">
        <label htmlFor="name" className="form-label">
          Name
        </label>
        <input
          type="text"
          id="name"
          className="form-input"
          autoComplete="off"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
      </div>

      {/* New Dashboard Name Field */}
      <div className="form-group">
        <label htmlFor="dashboardName" className="form-label">
          Name Your Shared Expenses
        </label>
        <input
          type="text"
          id="dashboardName"
          className="form-input"
          autoComplete="off"
          value={dashboardNameState}
          onChange={(e) => setDashboardNameState(e.target.value)} // Only for frontend usage
        />
      </div>

      <button type="submit" className="form-button">
        Register
      </button>
    </form>
  );
};

export default RegistrationForm;

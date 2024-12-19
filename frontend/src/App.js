import React, { useEffect, useState } from "react";
import SidePanel from "./components/SidePanel";
import Dashboard from "./components/Dashboard";
import { getAllExpenses } from "./api/api";
import "./styles/styles.css";

const App = () => {
  const [isRegistered, setIsRegistered] = useState(false);
  const [username, setUsername] = useState("");
  const [members, setMembers] = useState([]);
  const [expenses, setExpenses] = useState([]);
  const [dashboardName, setDashboardName] = useState("");

  // Fetch expenses from the server when the component mounts
  useEffect(() => {
    fetchExpenses();
  }, []);

  const fetchExpenses = async () => {
    try {
      const response = await getAllExpenses();
      setExpenses(response.data || []);
      console.log("Fetched expenses:", response.data); // Debugging log
    } catch (error) {
      console.error("Error fetching expenses:", error);
      setExpenses([]); // Set default fallback
    }
  };

  // Debugging log to check dashboardName
  console.log("App dashboardName:", dashboardName); // Check the state value

  return (
    <div className="app">
      <div className="main-content">
        <SidePanel
          isRegistered={isRegistered}
          setIsRegistered={setIsRegistered}
          username={username}
          setUsername={setUsername}
          setDashboardName={setDashboardName} // Passing down the setter for dashboardName
          members={members}
          setMembers={setMembers}
          expenses={expenses}
          setExpenses={setExpenses}
          fetchExpenses={fetchExpenses}
        />
        <Dashboard
          members={members}
          expenses={expenses}
          username={username} // Passing username prop
          setExpenses={setExpenses}
          dashboardName={dashboardName} // Passing the state value for dashboardName to Dashboard
        />
      </div>
    </div>
  );
};

export default App;

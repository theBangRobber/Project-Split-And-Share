import React, { useState, useEffect } from "react";
import { addGroupMembers } from "../api/api";

const AddMemberForm = ({ username, setMembers }) => {
  const [memberNames, setMemberNames] = useState([""]);
  const [error, setError] = useState("");

  useEffect(() => {
    console.log("Username passed to AddMemberForm:", username); // Debugging line
  }, [username]);

  const handleInputChange = (index, value) => {
    const newMemberNames = [...memberNames];
    newMemberNames[index] = value;
    setMemberNames(newMemberNames);
  };

  const handleAddMemberField = () => {
    setMemberNames([...memberNames, ""]);
  };

  const handleRemoveMemberField = (index) => {
    const newMemberNames = memberNames.filter((_, i) => i !== index);
    setMemberNames(newMemberNames);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (memberNames.some((name) => name === "")) {
      setError("All member names must be filled.");
      return;
    }
    if (!username) {
      setError("Username is missing.");
      return;
    }
    try {
      const response = await addGroupMembers(username, memberNames);
      setMembers(response.data);
      setMemberNames([""]);
    } catch (err) {
      setError("Error adding members. Please try again.");
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="dynamic-form add-member-form"
    >
      <h2 className="form-title">Add Group Members</h2>
      {error && <p className="error-message">{error}</p>}
      {memberNames.map((memberName, index) => (
        <div className="form-group" key={index}>
          <label
            htmlFor={`memberName${index}`}
            className="form-label"
          >
            Member Name {index + 1}
          </label>
          <input
            type="text"
            id={`memberName${index}`}
            className="form-input"
            autoComplete="off"
            value={memberName}
            onChange={(e) => handleInputChange(index, e.target.value)}
            required
          />
          <button
            type="button"
            className="form-button"
            onClick={() => handleRemoveMemberField(index)}
          >
            Remove
          </button>
        </div>
      ))}
      <button
        type="button"
        className="form-button"
        onClick={handleAddMemberField}
      >
        Add Another Member
      </button>
      <button type="submit" className="form-button">
        Submit
      </button>
    </form>
  );
};

export default AddMemberForm;

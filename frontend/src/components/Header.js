import React from "react";

const Header = ({ title, subtitle }) => {
  return (
    <header className="header">
      <h1>{title}</h1>
      <h2>{subtitle}</h2>
    </header>
  );
};

export default Header;

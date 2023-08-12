import React, { Fragment } from "react";
import "./Navbar.css";
import { FaBars } from "react-icons/fa";

const Navbar = () => {
  return (
    <Fragment>
      <nav className="nav">
        <div className="navbar-container">
          <a className="logo" href="/">
            Subtly
          </a>
          <div className="mobile-icon">
            <FaBars />
          </div>
          <ul className="nav-menu">
            <li className="nav-item">
              <a className="nav-link active" href="/">
                View
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link active" href="/upload">
                Upload
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link active" href="/generate">
                Generate
              </a>
            </li>
          </ul>
        </div>
      </nav>
    </Fragment>
  );
};

export default Navbar;

import { Suspense } from "react";
import "./App.css";
import Navbar from "./components/Navbar/Navbar.js";
import View from "./components/View/View.js";
import Form from "./components/Upload/Form.js";
import Generate from "./components/Generate/Generate.js";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Edit from "./components/Edit/Edit";

function App() {
  return (
    <Suspense>
      <Router>
        <Navbar />
        <Routes>
          <Route exact path="/" Component={View} />
          <Route exact path="/upload" Component={Form} />
          <Route exact path="/generate" Component={Generate} />
          <Route exact path="/edit" Component={Edit} />
        </Routes>
      </Router>
    </Suspense>
  );
}

export default App;

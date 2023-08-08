import React, { useState } from "react";
import "./Edit.css";
import { getServerUrl, put } from "../../utils/CRUD";
import { useNavigate, useLocation } from "react-router-dom";


const Edit = ({ id }) => {
  const [transcript, setTranscript] = useState("");
  const [focused, setFocused] = useState(false);
  const location = useLocation()
  const navigate = useNavigate();

  const handleFocus = (e) => {
    setFocused(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();



    // Write the put request to the controller here
    const { responseStatus, responseBody } = await put(getServerUrl(), location.state.id, { transcript: transcript });
    if (responseStatus !== 200) {
      alert(`Sorry, update failed due to an error. Please check your connection. Error info:${JSON.stringify(responseBody)}`);
    }
    else {
      navigate("/");
    }
  };

  return (
    <div className="edit-container">
      <form className="edit-form" onSubmit={handleSubmit}>
        <h1 className="edit-h1">Edit Transcript</h1>
        <div className="edit-forminput">
          <label className="edit-lable">New Transcript</label>
          <input
            className="edit-input"
            onBlur={handleFocus}
            focused={focused.toString()}
            placeholder="Write new Transcript"
            name="edit"
            value={transcript}
            onChange={(e) => setTranscript(e.target.value)}
          />
        </div>

        <button className="edit-submit">Submit</button>
      </form>
    </div>
  );
};

export default Edit;

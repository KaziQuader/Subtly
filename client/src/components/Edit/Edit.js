import React, { useState } from "react";
import "./Edit.css";

const Edit = () => {
  const [edit, setEdit] = useState("");
  const [focused, setFocused] = useState(false);
  const handleFocus = (e) => {
    setFocused(true);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const newTranscript = { edit };
    console.log(newTranscript);

    // Write the put request to the controller here
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
            value={edit}
            onChange={(e) => setEdit(e.target.value)}
          />
        </div>

        <button className="edit-submit">Submit</button>
      </form>
    </div>
  );
};

export default Edit;

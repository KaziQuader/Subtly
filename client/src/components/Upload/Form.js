import React, { useState } from "react";
import "./Form.css";
import FormInput from "./FormInput.js";
import { useNavigate } from "react-router-dom";
import { getServerUrl, post } from "../../utils/CRUD";

const Form = () => {
  const [values, setValues] = useState({
    transcript: "",
    file: ""
  });

  const navigate = useNavigate();

  const inputs = [
    {
      id: 1,
      name: "transcript",
      type: "text",
      placeholder: "Write Your Transcript",
      errorMessage:
        "Transcript should be minimum of 3 characters and shouldn't include any special character!",
      label: "Transcript",
      pattern: "^[A-Za-z0-9][A-Za-z0-9\\s]{2,}$",
      required: true,
    },
    {
      id: 2,
      name: "file",
      type: "file",
      placeholder: "Upload Your File",
      errorMessage: "File type does not match",
      label: "Audio File",
      required: true,
    },
  ];

  const onChange = (e) => {
    if (e.target.name === "file") {
      setValues({ ...values, file: e.target.files[0] });

    } else {
      setValues({ ...values, transcript: e.target.value });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Write the post request to the controller here
    const form = new FormData();
    form.append("transcript", values.transcript);
    form.append("file", values.file);

    const { responseStatus, responseBody } = await post(getServerUrl(), null, form);
    if (responseStatus !== 200) {
      alert("Error submitting audio data", responseBody)
    }
    else {
      console.log("hit!!!")
      navigate("/")
    }
  };

  return (
    <div className="app">
      <form onSubmit={handleSubmit}>
        <h1>Upload Your Audio</h1>
        {inputs.map((input) => (
          <FormInput
            key={input.id}
            {...input}
            value={input.type === "file" ? undefined : values[input.name]}
            onChange={onChange}
          />
        ))}
        <button>Submit</button>
      </form>
    </div>
  );
};

export default Form;

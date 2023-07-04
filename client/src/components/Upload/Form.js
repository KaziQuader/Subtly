import { useState } from "react";
import "./Form.css";
import FormInput from "./FormInput.js";

const Form = () => {
  const [values, setValues] = useState({
    transcript: "",
    audioFile: "",
  });

  const inputs = [
    {
      id: 1,
      name: "Transcript",
      type: "text",
      placeholder: "Write Your Transcript",
      errorMessage:
        "Transcript should be 3-16 characters and shouldn't include any special character!",
      label: "Transcript",
      pattern: "^[A-Za-z0-9]{3,16}$",
      required: true,
    },
    {
      id: 2,
      name: "Audio File",
      type: "file",
      placeholder: "Upload Your File",
      errorMessage: "File type doesnot match",
      label: "Audio File",
      required: true,
    },
  ];

  const handleSubmit = (e) => {
    e.preventDefault();
  };

  const onChange = (e) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };
  return (
    <div className="app">
      <form onSubmit={handleSubmit}>
        <h1>Upload Your Audio</h1>
        {inputs.map((input) => (
          <FormInput
            key={input.id}
            {...input}
            value={values[input.name]}
            onChange={onChange}
          />
        ))}
        <button>Submit</button>
      </form>
    </div>
  );
};

export default Form;

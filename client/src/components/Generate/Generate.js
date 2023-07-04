import { useState } from "react";
import "./Generate.css";
import GenerateInput from "./GenerateInput.js";

const Generate = () => {
  const [values, setValues] = useState({
    audioFile: "",
  });

  const inputs = [
    {
      id: 1,
      name: "Audio File / Video File",
      type: "file",
      placeholder: "Upload Your File",
      errorMessage: "File type doesnot match",
      label: "Audio File / Video File",
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
        <h1>Upload Audio/Video to Generate Subtitle</h1>
        {inputs.map((input) => (
          <GenerateInput
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

export default Generate;

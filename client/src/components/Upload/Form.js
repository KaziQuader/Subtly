import { useState } from "react";
import "./Form.css";
import FormInput from "./FormInput.js";

const Form = () => {
  const [values, setValues] = useState({
    transcript: "",
    audioFile: "",
  });

  const [transcript, setTranscript] = useState("");
  const [audioFile, setAudioFile] = useState();

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

  const onChange = (e) => {
    if (e.target.name === "Audio File") {
      const reader = new FileReader();

      reader.onload = () => {
        if (reader.readyState === 2) {
          setAudioFile(reader.result);
        }
      };
      reader.readAsDataURL(e.target.files[0]);
    } else {
      setTranscript({ [e.target.name]: e.target.value });
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // Data has transcript name and the audio file
    const data = { transcript, audioFile };
    console.log(data);
    // Write the post request to the controller here
    const form = new Form();

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

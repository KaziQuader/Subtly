import React, { useState } from "react";
import "./Generate.css";
import GenerateInput from "./GenerateInput.js";
import { post, getServerUrl, get } from '../../utils/CRUD'
import { useNavigate } from "react-router-dom"
const Generate = () => {
  const [values, setValues] = useState({
    file: "",
  });
  const navigate = useNavigate()
  const [progress, setProgress] = useState(0)
  const [state, setState] = useState("Started")

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
  const sleep = ms => new Promise(r => setTimeout(r, ms));
  const handleSubmit = async (e) => {
    e.preventDefault();
    // Write the post request to the controller here
    const form = new FormData();

    form.append("file", values.file);

    const { responseStatus, responseBody } = await post(getServerUrl() + "/transcript/submit", null, form);

    if (responseStatus !== 200)
      alert("Error submitting audio data", responseBody)
    else {
      while (true) {
        const { responseStatus: progressStatus, responseBody: progressResponse } = await get(getServerUrl() + "/transcript", responseBody["taskId"], null);
        if (progressStatus === 200) {
          setProgress(progressResponse["progress"])
          setState(progressResponse["state"])
          if (progressResponse["state"] === "complete") {
            window.location.replace(`${getServerUrl()}/transcript/${responseBody["taskId"]}/input.srt`);
            navigate("/")
            break;
          }
        }
        await sleep(1000);
      }

    }
  };

  const onChange = (e) => {
    setValues({ ...values, file: e.target.files[0] });
  };
  return (
    <div className="generate-container">
      <form className="generate-form" onSubmit={handleSubmit}>
        {state === "Started" && <div>

          <h1 className="generate-h1">Upload Audio/Video to Generate Subtitle</h1>
          {inputs.map((input) => (
            <GenerateInput
              key={input.id}
              {...input}
              value={values["type"] === "file" ? undefined : values[input.name]}
              onChange={onChange}
            />
          ))}
        </div>}

        {state !== "Started" && <h1>{state + " : " + progress + "%"}</h1>}
        {state === "Started" && <button className="generate-button">Submit</button>}
      </form>
    </div >
  );
};

export default Generate;

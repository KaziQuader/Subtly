import React from "react";
import AudioCard from "../AudioCard/AudioCard";
import "./View.css";
import Stack from "@mui/material/Stack";
import Container from "@mui/material/Container";

const View = () => {
  return (
    <Container fixed>
      <div className="container">
        <Stack className="stack" spacing={2}>
          <AudioCard />
          <AudioCard />
          <AudioCard />
        </Stack>
      </div>
    </Container>
  );
};

export default View;

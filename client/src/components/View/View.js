import React from "react";
import AudioCard from "../AudioCard/AudioCard";
import "./View.css";
import Stack from "@mui/material/Stack";
import Container from "@mui/material/Container";
import useFetch from "../../utils/useFetch";
import { getServerUrl } from "../../utils/CRUD";

const View = () => {
  let { data, isPending, error } = useFetch(getServerUrl());


  return (
    <Container fixed>
      {data && data.length > 0 && <Stack className="stack" spacing={5}>
        {data.map((audio) => <AudioCard transcription={audio.transcript} audioFileUrl={audio.fileUri} />
        )}
      </Stack>}
      {isPending && <div> Loading </div>}
      {(error || data.length === 0) && <div> Sorry no data available, check your internet connection.</div>}
    </Container>
  );
};

export default View;

import React from "react";
import AudioCard from "../AudioCard/AudioCard";
import "./View.css";
import Stack from "@mui/material/Stack";
import Container from "@mui/material/Container";
import useFetch from "../../utils/useFetch";
import { getServerUrl } from "../../utils/CRUD";

const View = () => {
  const { data: audioList, isPending, error } = useFetch(getServerUrl());
  return (
    <Container fixed>
      {audioList && audioList.length > 0 && <Stack className="stack" spacing={5}>
        {audioList.map((audio) => <AudioCard key={audio.id} id={audio.id} transcript={audio.transcript} fileUri={audio.fileUri} />
        )}
      </Stack>}
      {isPending && <div> Loading </div>}
      {(audioList && audioList.length === 0) && <div>DB not populated</div>}
      {error && <div> Sorry no data available, check your internet connection.</div>}
    </Container>
  );
};

export default View;

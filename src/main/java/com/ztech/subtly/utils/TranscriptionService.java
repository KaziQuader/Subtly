package com.ztech.subtly.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TranscriptionService {
    private enum State {
        EXTRACTING,
        TRANSCRIPTING,
        INTERNAL_ERROR,
        COMPLETED,
        BAD_REQUEST
    };

    private String fileUri;

    public TranscriptionService(
            String fileUri) {
        this.fileUri = fileUri;

    }

    public ResponseEntity<String> generateTranscript(String mimeType) {
        State state = State.BAD_REQUEST;
        if (mimeType.contains("video")) {
            msg = saveState(State.TRANSCRIPTING);
        } else if (mimeType.contains("video")) {
            msg = saveState(State.EXTRACTING);
        }
        return new ResponseEntity<String>("", null, HttpStatus.BAD_REQUEST);
    }

    private void extractAudio() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("myCommand", "myArg1", "myArg2");
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
        }
    }

    private boolean saveState(State state) {
        Map<String, Object> map = new HashMap<>();
        switch (state) {
            case EXTRACTING:
                map.put("state", "extracting");
                break;
            case TRANSCRIPTING:
                map.put("state", "transcripting");
                break;
            case INTERNAL_ERROR:
                map.put("state", "internal server error");
                break;
            case COMPLETED:
                map.put("state", "completed");
                break;
            default:
                map.put("state", "bad request");
                break;
        }

        try {
            new ObjectMapper().writeValue(Paths.get(".json").toFile(), map);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;

    }

}

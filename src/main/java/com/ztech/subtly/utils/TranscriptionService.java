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
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
// cat file.txt | grep -o -E time=[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]* | tail -1
// 
public class TranscriptionService {
    private enum State {
        EXTRACTING,
        TRANSCRIPTING,
        INTERNAL_ERROR,
        COMPLETED,
        BAD_REQUEST
    };

    private String fileUri;
    private String uploadFolder;

    public TranscriptionService(
            String fileUri, String uploadFolder) {
        this.fileUri = fileUri;
        this.uploadFolder = uploadFolder;
    }

    public void getStatus() {
        BufferedReader reader = new BufferedReader();
    }

    public ResponseEntity<Map<String, Object>> generateTranscript(String mimeType) {
        Map<String, Object> response = null;
        if (mimeType.contains("audio")) {
            response = saveState(State.TRANSCRIPTING);
        } else if (mimeType.contains("video")) {
            response = saveState(State.EXTRACTING);
        } else {
            response = saveState(State.BAD_REQUEST);
        }

        if (response == null) {
            response = new HashMap<String, Object>();
            response.put("state", "internal_server_error");
            return new ResponseEntity<Map<String, Object>>(response, null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<Map<String, Object>>(response, null, HttpStatus.BAD_REQUEST);
    }

    private void extractAudio() {
        try {
            new ProcessBuilder("myCommand", "myArg1", "myArg2").start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startTranscription() {
        try {
            new ProcessBuilder("myCommand", "myArg1", "myArg2").start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Map<String, Object> saveState(State state) {
        Map<String, Object> map = new HashMap<>();
        String taskId = UUID.randomUUID().toString();
        map.put("task_id", taskId);
        switch (state) {
            case EXTRACTING:
                map.put("state", "extracting");
                extractAudio();
                break;
            case TRANSCRIPTING:
                map.put("state", "transcripting");
                startTranscription();
                break;
            case INTERNAL_ERROR:
                map.put("state", "internal_server_error");
                break;
            case COMPLETED:
                map.put("state", "completed");
                break;
            default:
                map.put("state", "bad_request");
                break;
        }

        try {
            new ObjectMapper().writeValue(Paths.get(this.uploadFolder, taskId + ".json").toFile(), map);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return map;

    }

}

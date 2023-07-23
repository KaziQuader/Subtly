package com.ztech.subtly.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
// cat file.txt | grep -E -o  'DURATION.*$' | tail -1 | grep -E -o '[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]*$'
// cat file.txt | grep -E -o '.*time=.*'| grep -E -o '[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]*'| tail -1

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

    public void extractAudio() {
        String[] command = new String[] { "ffmpeg", "-y", "-vn", "-i", "input.mp4", "input.wav" };

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(uploadFolder));
            pb.redirectError(new File(uploadFolder + "/extract.log"));
            pb.redirectOutput(new File(uploadFolder + "/extract.log"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getExtractionStatus() {
        // cat file.txt | grep -E -o 'DURATION.*$' | tail -1 | grep -E -o
        // '[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]*$'
        // cat file.txt | grep -E -o '.*time=.*'| grep -E -o
        // '[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]*'| tail -1

        try {
            String command[] = new String[] { "cat", "extract.log" };
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(uploadFolder));
            InputStreamReader inputStreamReader = new InputStreamReader(pb.start().getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            Pattern pattern = Pattern
                    .compile(
                            "Duration:\s[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{2}|time=[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{2}");
            String duration = null;
            String time = null;
            while ((line = bufferedReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    if (matcher.group().startsWith("Duration"))
                        duration = matcher.group();
                    else
                        time = matcher.group();
                    System.out.println(matcher.group());
                }

            }
            System.out.println(duration + " " + time);
            if (duration != null) {
                pattern = Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{2}");
                Matcher matcher1 = pattern.matcher(duration);
                Matcher matcher2 = pattern.matcher(time);
                if (matcher1.find() && matcher2.find()) {
                    duration = matcher1.group();
                    time = matcher2.group();

                    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");// dd/MM/yyyy
                    long date = sdfDate.parse("1970-01-01 " + time).toInstant().toEpochMilli();
                    System.out.println(date);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void transcribeAudio() {
        String[] command = new String[] { "whisper", "--model", "tiny.en", "--model_dir", "../../lang_models/",
                "--output_format", "srt", "--task", "transcribe", "input.wav" };

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(uploadFolder));
            pb.redirectError(new File(uploadFolder + "/transcribe.log"));
            pb.redirectOutput(new File(uploadFolder + "/transcribe.log"));

        } catch (Exception e) {
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
                transcribeAudio();
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

package com.ztech.subtly.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public class TranscriptionService {

    private String processingFolder;
    private String taskId;
    private String fileName;

    public TranscriptionService(
            MultipartFile file) {
        this.taskId = UUID.randomUUID().toString();
        this.processingFolder = System.getProperty("user.dir") + "/processing/" + taskId;
        this.fileName = file.getOriginalFilename();
        StorageService storageService = new StorageService();
        storageService.save(file, processingFolder);

    }

    public TranscriptionService(String taskId) {
        this.taskId = taskId;
        this.processingFolder = System.getProperty("user.dir") + "/processing/" + taskId;
    }

    public ResponseEntity<Map<String, Object>> generateTranscript(String mimeType) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("taskId", this.taskId);
        if (mimeType.contains("audio")) {
            response.put("state", "transcripting");
            transcribeAudio();

        } else if (mimeType.contains("video")) {
            response.put("state", "extracting");
            new Thread(null, new Runnable() {
                public void run() {
                    try {
                        Process audioExtraction = extractAudio();
                        audioExtraction.waitFor();
                        transcribeAudio();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();

        } else {
            response.put("state", "bad_request");
            response.put("msg", "please submit a video or audio file");
            return new ResponseEntity<Map<String, Object>>(response, null, HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<Map<String, Object>>(response, null, HttpStatus.OK);
    }

    public Process extractAudio() {
        String[] command = new String[] { "ffmpeg", "-y", "-vn", "-i", fileName, "input.wav" };

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(processingFolder));
            pb.redirectError(new File(processingFolder + "/extract.log"));
            pb.redirectOutput(new File(processingFolder + "/extract.log"));
            return pb.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Process transcribeAudio() {
        String[] command = new String[] { "whisper", "--model", "tiny.en", "--model_dir", "../../lang_models/",
                "--output_format", "srt", "--task", "transcribe", "input.wav" };

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(processingFolder));
            pb.redirectError(new File(processingFolder + "/transcribe.log"));
            pb.redirectOutput(new File(processingFolder + "/transcribe.log"));
            return pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<Map<String, Object>> getStatus() {
        File extractLog = new File(processingFolder + "/extract.log");
        File transcribeLog = new File(processingFolder + "/transcribe.log");
        File srtFile = new File(processingFolder + "/input.srt");
        Map<String, Object> response = new HashMap<String, Object>();
        ResponseEntity<Map<String, Object>> responseEntity;
        if (srtFile.exists()) {
            response.put("state", "complete");
            response.put("fileUri", srtFile);
            responseEntity = new ResponseEntity<Map<String, Object>>(response, null, HttpStatus.OK);

        } else if (transcribeLog.exists()) {
            response.put("state", "transcripting");
            response.put("progress", getTranscriptionProgress());
            responseEntity = new ResponseEntity<Map<String, Object>>(response, null, HttpStatus.OK);

        } else if (extractLog.exists()) {
            response.put("state", "extracting");
            response.put("progress", getExtractionProgress());
            responseEntity = new ResponseEntity<Map<String, Object>>(response, null, HttpStatus.OK);

        } else {
            response.put("state", "internal_server_error");
            responseEntity = new ResponseEntity<Map<String, Object>>(response, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    private double getExtractionProgress() {
        try {
            String command[] = new String[] { "cat", "extract.log" };
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(processingFolder));
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
                }

            }
            if (duration != null && time != null) {
                pattern = Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{2}");
                Matcher matcher1 = pattern.matcher(duration);
                Matcher matcher2 = pattern.matcher(time);
                if (matcher1.find() && matcher2.find()) {
                    duration = matcher1.group();
                    time = matcher2.group();

                    double millisCurrentTime = extractSeconds(time);
                    double millisTotalTime = extractSeconds(duration);
                    double extractionProgress = millisCurrentTime * 100 / millisTotalTime;

                    return extractionProgress;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private double getTranscriptionProgress() {
        try {
            String command[] = new String[] { "cat", "transcribe.log" };
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(processingFolder));
            InputStreamReader inputStreamReader = new InputStreamReader(pb.start().getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = "";
            Pattern pattern = Pattern
                    .compile(
                            "duration=[0-9]+\\.[0-9]+|[0-9]{2}:[0-9]{2}\\.[0-9]{3}");
            String duration = null;
            String time = null;
            while ((line = bufferedReader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    if (matcher.group().startsWith("duration="))
                        duration = matcher.group();
                    else
                        time = matcher.group();
                }

            }
            if (duration != null && time != null) {
                pattern = Pattern.compile("[0-9]{2}:[0-9]{2}\\.[0-9]{3}");
                Matcher timeMatcher = pattern.matcher(time);
                if (timeMatcher.find()) {
                    time = timeMatcher.group();

                    double millisCurrentTime = extractSeconds(time);
                    double millisTotalTime = Double.parseDouble(duration.replace("duration=", "")) * 60;
                    double transcriptionProgress = millisCurrentTime * 100.0 / millisTotalTime;
                    return transcriptionProgress;

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private double extractSeconds(String time) {
        String[] timeParts = time.split(":");
        double seconds = 0.0;
        if (timeParts.length > 2) {
            seconds = Double.parseDouble(timeParts[0]) * 3600; // hours to seconds
            seconds += Double.parseDouble(timeParts[1]) * 60; // minutes to seconds
            seconds += Double.parseDouble(timeParts[2]); // seconds

        } else {

            seconds = Double.parseDouble(timeParts[0]) * 3600; // hours to seconds
            seconds += Double.parseDouble(timeParts[1]) * 60; // minutes to seconds
        }
        return seconds;
    }

}

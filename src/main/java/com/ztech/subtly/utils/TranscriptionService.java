package com.ztech.subtly.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class TranscriptionService {
    private String fileUri;
    private String uploadFolder;
    private String taskId;

    public TranscriptionService(
            String fileUri, String uploadFolder, String taskId) {
        this.fileUri = fileUri;
        this.uploadFolder = uploadFolder;
        this.taskId = taskId;
    }

    public TranscriptionService(String taskId) {

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
        String[] command = new String[] { "ffmpeg", "-y", "-vn", "-i", fileUri, "input.wav" };

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(uploadFolder));
            pb.redirectError(new File(uploadFolder + "/extract.log"));
            pb.redirectOutput(new File(uploadFolder + "/extract.log"));
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
            pb.directory(new File(uploadFolder));
            pb.redirectError(new File(uploadFolder + "/transcribe.log"));
            pb.redirectOutput(new File(uploadFolder + "/transcribe.log"));
            return pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<Map<String, Object>> getSrtFileUri() {
        String srtFileUri = uploadFolder + "/input.srt";
        File file = new File(srtFileUri);
        Map<String, Object> response = new HashMap<String, Object>();
        if (file.exists()) {
            response.put("fileUri", srtFileUri);
            return new ResponseEntity<Map<String, Object>>(response, null, HttpStatus.OK);
        }
        return getStatus();
    }

    private ResponseEntity<Map<String, Object>> getStatus() {
        File extractLog = new File(uploadFolder + "/extract.log");
        File transcribeLog = new File(uploadFolder + "/transcribe.log");
        Map<String, Object> response = new HashMap<String, Object>();
        if (transcribeLog.exists()) {
            response.put("state", "transcripting");
            response.put("progress", getTranscriptionProgress());
            return new ResponseEntity<Map<String, Object>>(response, null, HttpStatus.OK);

        } else if (extractLog.exists()) {
            response.put("state", "extracting");
            response.put("progress", getExtractionProgress());
            return new ResponseEntity<Map<String, Object>>(response, null, HttpStatus.OK);

        }

        response.put("state", "bad_request");
        return new ResponseEntity<Map<String, Object>>(response, null, HttpStatus.BAD_REQUEST);

    }

    private double getExtractionProgress() {
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
            pb.directory(new File(uploadFolder));
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

package com.ztech.subtly.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.ztech.subtly.model.AudioData;
import com.ztech.subtly.utils.StorageService;
import com.ztech.subtly.utils.TranscriptionService;
import com.ztech.subtly.controller.repository.AudioDataRepository;

@RestController
@RequestMapping(path = "/api/v1/audio")
public class AudioDataController {
    @Autowired
    private AudioDataRepository audioDataRepository;
    private StorageService storageService;

    public AudioDataController(AudioDataRepository audioDataRepository) {
        this.audioDataRepository = audioDataRepository;
        this.storageService = new StorageService();
    }

    /**
     * TranscriptUpdateRequest
     * String transcript
     */
    record TranscriptUpdateRequest(String transcript) {
    }

    @GetMapping
    public @ResponseBody List<AudioData> getAllAudio() {
        return audioDataRepository.findAll();
    }

    @GetMapping(path = "/{audio_id}")
    public @ResponseBody AudioData getAudio(@PathVariable("audio_id") Integer id) {
        return audioDataRepository.findById(id).get();
    }

    @GetMapping(path = "/file/{file_id}")
    public @ResponseBody ResponseEntity<StreamingResponseBody> getAudioFile(@PathVariable("file_id") String file_id,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        String path = System.getProperty("user.dir") + "/uploads/" + file_id;
        return storageService.serveMediaFile(path, rangeHeader);
    }

    @PostMapping
    public ResponseEntity<String> addAudioData(@RequestParam("transcript") String transcript,
            @RequestParam("file") MultipartFile file) {
        String uploadFolder = System.getProperty("user.dir") + "/uploads/";
        String fileUri = storageService.save(file, uploadFolder);

        if (fileUri != null) {
            AudioData audioData = new AudioData();
            audioData.setTranscript(transcript);
            audioData.setFileUri(file.getOriginalFilename());
            audioDataRepository.save(audioData);
            return ResponseEntity.ok().body(null);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/transcript/submit")
    public ResponseEntity<Map<String, Object>> submitFileForTranscription(@RequestParam("file") MultipartFile file) {
        String taskId = UUID.randomUUID().toString();
        String uploadFolder = System.getProperty("user.dir") + "/processing/" + taskId + "/";
        String fileUri = storageService.save(file, uploadFolder);
        return new TranscriptionService(fileUri, uploadFolder, taskId).generateTranscript(file.getContentType());
    }

    @GetMapping(path = "/transcript/{taskId}")
    public @ResponseBody ResponseEntity<StreamingResponseBody> getSrtFile(@PathVariable("taskId") String taskId) {
        String path = System.getProperty("user.dir") + fileUri;
        return storageService.serveMediaFile(path, rangeHeader);
    }

    @GetMapping(path = "/transcript/file/{fileUri}")
    public @ResponseBody ResponseEntity<StreamingResponseBody> getSrtFile(@PathVariable("fileUri") String fileUri,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        String path = System.getProperty("user.dir") + fileUri;
        return storageService.serveMediaFile(path, rangeHeader);
    }

    @PutMapping(path = "/{audio_id}")
    public void updateAudioTranscript(@PathVariable("audio_id") Integer id,
            @RequestBody TranscriptUpdateRequest transcriptUpdateRequest) {
        AudioData audioData = audioDataRepository.findById(id).get();
        audioData.setTranscript(transcriptUpdateRequest.transcript());
        audioDataRepository.save(audioData);
    }
}
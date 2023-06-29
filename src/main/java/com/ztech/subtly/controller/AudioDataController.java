package com.ztech.subtly.controller;

import java.util.List;

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

import jakarta.servlet.http.HttpServletRequest;

import com.ztech.subtly.controller.repository.AudioDataRepository;

@RestController
@RequestMapping(path = "/api/v1/audio")
public class AudioDataController {
    @Autowired
    private HttpServletRequest request;
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
        String path = System.getProperty("user.dir") + "\\uploads\\" + file_id;
        return storageService.serveMediaFile(path, rangeHeader);
    }

    @PostMapping
    public ResponseEntity<String> addAudioData(@RequestParam("transcript") String transcript,
            @RequestParam("file") MultipartFile file) {
        String uploadFolder = System.getProperty("user.dir") + "\\uploads\\";
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

    @PutMapping(path = "/{audio_id}")
    public void updateAudioTranscript(@PathVariable("audio_id") Integer id,
            @RequestBody TranscriptUpdateRequest transcriptUpdateRequest) {
        AudioData audioData = audioDataRepository.findById(id).get();
        audioData.setTranscript(transcriptUpdateRequest.transcript());
        audioDataRepository.save(audioData);
    }
}
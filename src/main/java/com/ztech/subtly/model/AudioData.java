package com.ztech.subtly.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity // This tells Hibernate to make a table out of this class

public class AudioData {
    @Id
    @SequenceGenerator(name = "audio_id_sequence", sequenceName = "audio_id_sequence", initialValue = 0, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)

    private Long id;
    private String fileUri;
    private String transcript;

    public AudioData() {
    }

    public AudioData(String fileUri, String transcript) {
        this.fileUri = fileUri;
        this.transcript = transcript;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getTranscript() {
        return this.transcript;
    }

    public String getFileUri() {
        return this.fileUri;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
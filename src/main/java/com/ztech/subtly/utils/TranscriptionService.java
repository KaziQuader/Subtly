package com.ztech.subtly.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class TranscriptionService {
    private String fileUri;
    private boolean isProcessing;
    private Thread processingThread;

    public TranscriptionService(
            String fileUri) {
        this.fileUri = fileUri;
        this.isProcessing = false;

    }

    public void generateTranscript() {
        this.toggleProcessing();
        this.processingThread = new Thread(new Runnable() {
            public void run() {

            }
        });

        this.processingThread.start();
    }

    private void extractAudio() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("myCommand", "myArg1", "myArg2");
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
        }
    }

    private void toggleProcessing() {
        this.isProcessing = !this.isProcessing;
    }

    public boolean getProcessing() {
        return this.isProcessing;
    }

    public void cancelTranscription() throws InterruptedException {
        this.processingThread.join();
    }

}

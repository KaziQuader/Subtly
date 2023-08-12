package com.ztech.subtly.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public class StorageService {
    private final static Logger log = LoggerFactory.getLogger(StorageService.class);

    public StorageService() {
    }

    public String save(MultipartFile file, String path) {
        return _save(file, path, file.getOriginalFilename());
    }

    public void saveAs(MultipartFile file, String path, String fileName) {
        _save(file, path, fileName);
    }

    private String _save(MultipartFile file, String path, String fileName) {
        try {
            if (!new File(path).exists()) {
                System.out.println("Path does not exist, creating...");
                new File(path).mkdirs();
            }
            String filePath = Paths.get(path, fileName).toString();
            byte[] buffer = new byte[1024];
            BufferedInputStream bufferedInputStream = new BufferedInputStream(file.getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            int count = 0;
            while ((count = bufferedInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, count);

            }
            fileOutputStream.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<StreamingResponseBody> serveMediaFile(String path, String rangeHeader) {
        try {
            StreamingResponseBody responseStream;
            Path filePath = Paths.get(path);
            Long fileSize = Files.size(filePath);
            byte[] buffer = new byte[1024];
            final HttpHeaders responseHeaders = new HttpHeaders();

            Long rangeStart, rangeEnd;
            rangeStart = 0L;
            rangeEnd = fileSize - 1L;

            if (rangeHeader == null) {
                responseHeaders.add("Content-Length", fileSize.toString());
            } else {

                String[] ranges = rangeHeader.split("-");
                rangeStart = Long.parseLong(ranges[0].substring(6)); // skipping bytes<space> part

                if (ranges.length > 1 && rangeEnd < fileSize) {
                    rangeEnd = Long.parseLong(ranges[1]);
                }

                String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
                responseHeaders.add("Content-Length", contentLength);
                responseHeaders.add("Accept-Ranges", "bytes");
                responseHeaders.add("Content-Range", "bytes" + " " +
                        rangeStart + "-" + rangeEnd + "/" + fileSize);
            }

            if (path.contains("audio"))
                responseHeaders.add("Content-Type", "audio/**");
            else if (path.contains("video"))
                responseHeaders.add("Content-Type", "video/**");

            final Long _rangeStart = rangeStart;
            final Long _rangeEnd = rangeEnd;

            responseStream = os -> {
                RandomAccessFile file = new RandomAccessFile(path, "r");
                try (file) {
                    long pos = _rangeStart;
                    file.seek(pos);
                    while (pos < _rangeEnd) {
                        file.read(buffer);
                        os.write(buffer);
                        pos += buffer.length;
                    }
                    os.flush();
                } catch (Exception e) {
                }
            };

            return new ResponseEntity<StreamingResponseBody>(responseStream, responseHeaders,
                    HttpStatus.PARTIAL_CONTENT);
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

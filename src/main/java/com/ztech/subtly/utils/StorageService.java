package com.ztech.subtly.utils;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class StorageService {
    private final static Logger log = LoggerFactory.getLogger(StorageService.class);

    public StorageService() {
    }

    public String save(MultipartFile file, String path) {
        try {
            if (!new File(path).exists()) {
                new File(path).mkdir();
            }

            log.info("path = {}", path);

            String filePath = path + file.getOriginalFilename();
            File dest = new File(filePath);
            file.transferTo(dest);
            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

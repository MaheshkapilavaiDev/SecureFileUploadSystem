package com.securefileuploadsystem.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    private final String UPLOAD_DIR =
            "uploads/";

    public String saveFile(
            MultipartFile file)
            throws IOException {

        String storedFileName =
                UUID.randomUUID()
                + "_"
                + file.getOriginalFilename();

        Path path = Paths.get(
                UPLOAD_DIR,
                storedFileName);

        Files.createDirectories(
                path.getParent());

        Files.write(
                path,
                file.getBytes());

        return storedFileName;
    }
}

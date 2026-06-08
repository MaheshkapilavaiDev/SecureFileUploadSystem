package com.securefileuploadsystem.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StorageService {
	
	@Autowired
	private EncryptionService encryptionService;

    private final String UPLOAD_DIR =
            "uploads/";

    public String saveEncryptedFile(
    		 byte[] encryptedBytes,
             String originalFileName)
            throws IOException {

        String storedFileName =
                UUID.randomUUID()
                + "_"
                + originalFileName;

        Path path = Paths.get(
                UPLOAD_DIR,
                storedFileName);

        Files.createDirectories(
                path.getParent());

        Files.write(
                path,
                encryptedBytes);

        return storedFileName;
    }
}

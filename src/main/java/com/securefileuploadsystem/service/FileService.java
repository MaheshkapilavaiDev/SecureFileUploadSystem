package com.securefileuploadsystem.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.securefileuploadsystem.entity.FileMetadata;
import com.securefileuploadsystem.repository.FileMetadataRepository;
import com.securefileuploadsystem.util.ChecksumUtil;
import com.securefileuploadsystem.util.FileValidationUtil;

@Service
public class FileService {

    @Autowired
    private FileMetadataRepository metadataRepo;

    @Autowired
    private StorageService storageService;

    public FileMetadata uploadFile(
            MultipartFile file)
            throws Exception {

        FileValidationUtil.validateFile(file);

        String checksum =
                ChecksumUtil
                .generateChecksum(file);

        if (metadataRepo
                .existsByChecksum(checksum)) {

            throw new RuntimeException(
                    "Duplicate file");
        }

        String storedFileName =
                storageService
                .saveFile(file);

        FileMetadata metadata =
                new FileMetadata();

        metadata.setFileId(
                UUID.randomUUID()
                .toString());

        metadata.setOriginalFileName(
                file.getOriginalFilename());

        metadata.setContentType(
                file.getContentType());

        metadata.setFileSize(
                file.getSize());

        metadata.setChecksum(
                checksum);

        metadata.setUploadTime(
                LocalDateTime.now());

        return metadataRepo
                .save(metadata);
    }
}

package com.securefileuploadsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.securefileuploadsystem.entity.FileMetadata;
import com.securefileuploadsystem.service.FileService;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileMetadata>
    uploadFile(
            @RequestParam("file")
            MultipartFile file)
            throws Exception {

        return ResponseEntity.ok(
                fileService.uploadFile(file));
    }
}

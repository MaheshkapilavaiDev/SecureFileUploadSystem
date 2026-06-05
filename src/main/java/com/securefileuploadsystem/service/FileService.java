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

import jakarta.transaction.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@Service
public class FileService {

	@Autowired
	private FileMetadataRepository metadataRepo;

	@Autowired
	private StorageService storageService;
	
	@Autowired
	private AuditLogService auditService;
	@Transactional
	public FileMetadata uploadFile(MultipartFile file) throws Exception {

		FileValidationUtil.validateFile(file);

		String checksum = ChecksumUtil.generateChecksum(file);

		if (metadataRepo.existsByChecksum(checksum)) {
			throw new RuntimeException("Duplicate file");
		}

		String storedFileName = storageService.saveFile(file);

		FileMetadata metadata = new FileMetadata();

		metadata.setFileId(UUID.randomUUID().toString());

		metadata.setOriginalFileName(file.getOriginalFilename());

		metadata.setStoredFileName(storedFileName);

		metadata.setContentType(file.getContentType());

		metadata.setFileSize(file.getSize());

		metadata.setChecksum(checksum);

		metadata.setEncrypted(false);

		metadata.setUploadedBy("Mahesh");

		metadata.setUploadTime(LocalDateTime.now());
		
		auditService.logAction(
		        metadata.getUploadedBy(),
		        "UPLOAD",
		        metadata.getFileId());

		return metadataRepo.save(metadata);
	}

	public Resource downloadFile(String fileId) throws Exception {

		FileMetadata metadata = metadataRepo.findByFileId(fileId)
				.orElseThrow(() -> new RuntimeException("File Not Found"));
		
		auditService.logAction(
		        metadata.getUploadedBy(),
		        "DOWNLOAD",
		        fileId);

		Path path = Paths.get("uploads").resolve(metadata.getStoredFileName());

		Resource resource = new UrlResource(path.toUri());

		if (!resource.exists()) {
			throw new RuntimeException("File not found on disk");
		}

		return resource;
	}
	
	@Transactional
	public String deleteFile(String fileId)
	        throws Exception {

	    FileMetadata metadata =
	            metadataRepo.findByFileId(fileId)
	            .orElseThrow(() ->
	                    new RuntimeException(
	                            "File Not Found"));

	    Path path = Paths.get("uploads")
	            .resolve(
	                    metadata.getStoredFileName());

	    Files.deleteIfExists(path);

	    metadataRepo.delete(metadata);

	    auditService.logAction(
	            metadata.getUploadedBy(),
	            "DELETE",
	            fileId);

	    return "File deleted successfully";
	}
}

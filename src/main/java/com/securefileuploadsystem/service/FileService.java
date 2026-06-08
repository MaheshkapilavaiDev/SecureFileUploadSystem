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

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class FileService {

	@Autowired
	private FileMetadataRepository metadataRepo;

	@Autowired
	private StorageService storageService;
	
	@Autowired
	private AuditLogService auditService;
	
	@Autowired
	private EncryptionService encryptionService;
	
	@Transactional
	public FileMetadata uploadFile(MultipartFile file) throws Exception {

		FileValidationUtil.validateFile(file);

		String checksum = ChecksumUtil.generateChecksum(file);

		if (metadataRepo.existsByChecksum(checksum)) {
			throw new RuntimeException("Duplicate file");
		}

		//String storedFileName = storageService.saveFile(file);
		
		byte[] encryptedBytes =
		        encryptionService.encrypt(
		                file.getBytes());

		String storedFileName =
		        storageService.saveEncryptedFile(
		                encryptedBytes,
		                file.getOriginalFilename());

		FileMetadata metadata = new FileMetadata();

		metadata.setFileId(UUID.randomUUID().toString());

		metadata.setOriginalFileName(file.getOriginalFilename());

		metadata.setStoredFileName(storedFileName);

		metadata.setContentType(file.getContentType());

		metadata.setFileSize(file.getSize());

		metadata.setChecksum(checksum);

		metadata.setEncrypted(true);

		//metadata.setUploadedBy("Mahesh");
		
		Authentication authentication =
		        SecurityContextHolder
		                .getContext()
		                .getAuthentication();

		String username =
		        authentication.getName();
		
		metadata.setUploadedBy(username);

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

		if (!Files.exists(path)) {
	        throw new RuntimeException(
	                "File not found on disk");
	    }

		 byte[] encryptedBytes =
		            Files.readAllBytes(path);

		    byte[] decryptedBytes =
		            encryptionService.decrypt(
		                    encryptedBytes);

		    return new ByteArrayResource(
		            decryptedBytes);
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

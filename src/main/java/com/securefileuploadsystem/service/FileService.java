package com.securefileuploadsystem.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.securefileuploadsystem.dto.FileDownloadResponse;
import com.securefileuploadsystem.entity.DownloadToken;
import com.securefileuploadsystem.entity.FileMetadata;
import com.securefileuploadsystem.repository.DownloadTokenRepository;
import com.securefileuploadsystem.repository.FileMetadataRepository;
import com.securefileuploadsystem.util.ChecksumUtil;
import com.securefileuploadsystem.util.FileValidationUtil;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.transaction.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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
	
	@Autowired
	private AsyncFileProcessingService asyncService;
	
	
	@Autowired
	private DownloadTokenRepository tokenRepo;
	
	@Autowired
	private RateLimitService rateLimitService;
	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private CacheMetadataService cacheMetaDataService;
	
	@Transactional
	public FileMetadata uploadFile(MultipartFile file) throws Exception {
		
		Authentication authentication =
	            SecurityContextHolder
	                    .getContext()
	                    .getAuthentication();

	    String username =
	            authentication.getName();

	    Bucket bucket =
	            rateLimitService.resolveBucket(username);

	    ConsumptionProbe probe =
	            bucket.tryConsumeAndReturnRemaining(1);

	    System.out.println(
	            "Consumed = " + probe.isConsumed()
	            + ", Remaining = "
	            + probe.getRemainingTokens());

	    if (!probe.isConsumed()) {
	        throw new RuntimeException(
	                "Upload limit exceeded");
	    }

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
		
		metadata.setUploadedBy(username);

		metadata.setUploadTime(LocalDateTime.now());
		
		auditService.logAction(
		        metadata.getUploadedBy(),
		        "UPLOAD",
		        metadata.getFileId());

		FileMetadata saved =metadataRepo.save(metadata);
		
		asyncService.processFile(saved.getFileId());
		
		return saved;
	}

	public Resource downloadFile(String fileId) throws Exception {

		//FileMetadata metadata = metadataRepo.findByFileId(fileId)
			//	.orElseThrow(() -> new RuntimeException("File Not Found"));
		
		FileMetadata metadata =
				cacheMetaDataService.getFileMetadata(fileId);
		
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

	   /* FileMetadata metadata =
	            metadataRepo.findByFileId(fileId)
	            .orElseThrow(() ->
	                    new RuntimeException(
	                            "File Not Found"));*/
		FileMetadata metadata =
	            cacheMetaDataService
	                    .getFileMetadata(fileId);

	    Path path = Paths.get("uploads")
	            .resolve(
	                    metadata.getStoredFileName());

	    Files.deleteIfExists(path);

	    metadataRepo.delete(metadata);
	    
	    cacheMetaDataService.removeFromCache(fileId);

	    auditService.logAction(
	            metadata.getUploadedBy(),
	            "DELETE",
	            fileId);

	    return "File deleted successfully";
	}
	
	public String generateDownloadUrl(
	        String fileId) {

	    DownloadToken token =
	            new DownloadToken();

	    token.setToken(
	            UUID.randomUUID().toString());

	    token.setFileId(fileId);

	    token.setExpiryTime(
	            LocalDateTime.now()
	                    .plusMinutes(5));

	    tokenRepo.save(token);

	    return token.getToken();
	}
	
	public FileDownloadResponse  downloadUsingToken(String token)
	        throws Exception {

	    DownloadToken downloadToken =
	            tokenRepo.findByToken(token)
	            .orElseThrow(() ->
	                    new RuntimeException("Invalid Token"));
	    
	    if (downloadToken.getExpiryTime()
	            .isBefore(LocalDateTime.now())) {

	        throw new RuntimeException(
	                "Download URL Expired");
	    }
	    
	    FileMetadata metadata =
	            cacheMetaDataService.getFileMetadata(
	                    downloadToken.getFileId());
	    
	    Resource resource =
	            downloadFile(downloadToken.getFileId());
	    

	    FileDownloadResponse response =
	            new FileDownloadResponse();

	    response.setMetadata(metadata);
	    response.setResource(resource);

	    return response;

	}
	public FileMetadata getMetadataByToken(String token) {

	    DownloadToken downloadToken =
	            tokenRepo.findByToken(token)
	                    .orElseThrow(() ->
	                            new RuntimeException("Invalid Token"));

	    return cacheMetaDataService
	            .getFileMetadata(downloadToken.getFileId());
	}
	
	public FileMetadata getFileMetadata(String fileId) {

	    return cacheMetaDataService.getFileMetadata(fileId);
	}
	
}

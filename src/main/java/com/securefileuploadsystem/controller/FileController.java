package com.securefileuploadsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.securefileuploadsystem.dto.FileDownloadResponse;
import com.securefileuploadsystem.entity.FileMetadata;
import com.securefileuploadsystem.repository.FileMetadataRepository;
import com.securefileuploadsystem.service.FileService;

import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/files")
public class FileController {

	@Autowired
	private FileService fileService;

	@Autowired
	private FileMetadataRepository metadataRepo;

	@PostMapping("/upload")
	public ResponseEntity<FileMetadata> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {

		return ResponseEntity.ok(fileService.uploadFile(file));
	}

	@GetMapping("/download/{fileId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) throws Exception {
		
		FileMetadata metadata =
	            fileService.getFileMetadata(fileId);

		Resource resource = fileService.downloadFile(fileId);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(metadata.getContentType()))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
				.body(resource);
	}
	@DeleteMapping("/{fileId}")
	public ResponseEntity<String> deleteFile(
	        @PathVariable String fileId)
	        throws Exception {

	    return ResponseEntity.ok(
	            fileService.deleteFile(fileId));
	}
	
	@GetMapping("/{fileId}/generate-url")
	public ResponseEntity<String>
	generateUrl(
	        @PathVariable String fileId) {

	    String token =
	            fileService
	                    .generateDownloadUrl(
	                            fileId);

	    return ResponseEntity.ok(
	            "http://localhost:8080/api/files/download?token="
	                    + token);
	}
	
	@GetMapping("/download")
	public ResponseEntity<Resource> download(
	        @RequestParam String token)
	        throws Exception {

		FileDownloadResponse  response =
	            fileService.downloadUsingToken(token);

		return ResponseEntity.ok()
	            .contentType(
	                    MediaType.parseMediaType(
	                            response.getMetadata()
	                                    .getContentType()))
	            .header(
	                    HttpHeaders.CONTENT_DISPOSITION,
	                    "attachment; filename=\""
	                            + response.getMetadata()
	                                    .getOriginalFileName()
	                            + "\"")
	            .body(response.getResource());
	}
}

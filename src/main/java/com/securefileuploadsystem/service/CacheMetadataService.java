package com.securefileuploadsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.securefileuploadsystem.entity.FileMetadata;
import com.securefileuploadsystem.repository.FileMetadataRepository;

@Service
public class CacheMetadataService {

	@Autowired
	private FileMetadataRepository metadataRepo;
	
	@Autowired
	private CacheManager cacheManager;

	@Cacheable(value = "files", key = "#fileId")
	public FileMetadata getFileMetadata(String fileId) {

		System.out.println("Fetching from DB...");

		return metadataRepo.findByFileId(fileId).orElseThrow(() -> new RuntimeException("File Not Found"));
	}
	@CacheEvict(value = "files", key = "#fileId")
    public void removeFromCache(String fileId) {
    }

}

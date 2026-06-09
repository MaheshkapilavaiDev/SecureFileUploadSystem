package com.securefileuploadsystem.dto;

import com.securefileuploadsystem.entity.FileMetadata;

import org.springframework.core.io.Resource;
public class FileDownloadResponse {

	private Resource resource;
	private FileMetadata metadata;

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public FileMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(FileMetadata metadata) {
		this.metadata = metadata;
	}

}

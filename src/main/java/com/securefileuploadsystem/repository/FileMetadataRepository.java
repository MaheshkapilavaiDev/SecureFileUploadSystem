package com.securefileuploadsystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.securefileuploadsystem.entity.FileMetadata;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

	boolean existsByChecksum(String checksum);

	Optional<FileMetadata> findByFileId(String fileId);
}

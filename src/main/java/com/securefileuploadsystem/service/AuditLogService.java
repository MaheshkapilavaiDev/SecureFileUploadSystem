package com.securefileuploadsystem.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.securefileuploadsystem.entity.AuditLog;
import com.securefileuploadsystem.repository.AuditLogRepository;

@Service
public class AuditLogService {

	@Autowired
	private AuditLogRepository auditRepo;

	public void logAction(String username, String action, String fileId) {

		AuditLog log = new AuditLog();

		log.setUsername(username);
		log.setAction(action);
		log.setFileId(fileId);
		log.setTimestamp(LocalDateTime.now());

		auditRepo.save(log);
	}

}

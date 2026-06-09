package com.securefileuploadsystem.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncFileProcessingService {

	@Async
	public void processFile(String fileId) {

		try {

			System.out.println("Background Processing Started: " + fileId);

			Thread.sleep(5000);

			System.out.println("Background Processing Completed: " + fileId);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
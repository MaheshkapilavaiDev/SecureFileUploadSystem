package com.securefileuploadsystem.util;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class FileValidationUtil {

	public static void validateFile(MultipartFile file) {

		if (file.isEmpty()) {
			throw new RuntimeException("File is empty");
		}

		if (file.getSize() > 5_000_000) {
			throw new RuntimeException("File size exceeds 5 MB");
		}

		List<String> allowedTypes = List.of("application/pdf", "image/png", "image/jpeg");

		if (!allowedTypes.contains(file.getContentType())) {

			throw new RuntimeException("Invalid file type");
		}
	}
}

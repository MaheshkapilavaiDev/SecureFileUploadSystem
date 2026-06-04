package com.securefileuploadsystem.util;

import java.security.MessageDigest;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

public class ChecksumUtil {

	public static String generateChecksum(MultipartFile file) throws Exception {

		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		byte[] hash = digest.digest(file.getBytes());

		return Base64.getEncoder().encodeToString(hash);
	}
}
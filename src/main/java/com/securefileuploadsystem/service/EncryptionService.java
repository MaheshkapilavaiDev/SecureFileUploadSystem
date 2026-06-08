package com.securefileuploadsystem.service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

    private static final String KEY =
            "1234567890123456";

    public byte[] encrypt(
            byte[] data) throws Exception {

        SecretKeySpec secretKey =
                new SecretKeySpec(
                        KEY.getBytes(),
                        "AES");

        Cipher cipher =
                Cipher.getInstance("AES");

        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey);

        return cipher.doFinal(data);
    }
    
    public byte[] decrypt(
            byte[] encryptedBytes)
            throws Exception {

        SecretKeySpec key =
                new SecretKeySpec(
                        KEY.getBytes(),
                        "AES");

        Cipher cipher =
                Cipher.getInstance("AES");

        cipher.init(
                Cipher.DECRYPT_MODE,
                key);

        return cipher.doFinal(
                encryptedBytes);
    }
}

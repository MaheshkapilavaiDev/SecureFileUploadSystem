# Secure File Upload System

## Overview

Secure File Upload System is a Spring Boot application that enables users to securely upload, download, and manage files. The application uses JWT authentication, AES encryption, Redis caching, audit logging, asynchronous processing, and rate limiting to ensure security and performance.

## Features

* JWT Authentication & Authorization
* Secure File Upload and Download
* AES File Encryption & Decryption
* Duplicate File Detection using Checksum
* Temporary Download URLs
* Audit Logging
* Async File Processing (@Async)
* Redis Caching
* Upload Rate Limiting (Bucket4j)
* File Delete Functionality

## Technology Stack

* Java 17
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* MySQL
* Redis
* Bucket4j
* Maven

## API Endpoints

### Authentication

* POST `/auth/login`

### File Operations

* POST `/api/files/upload`
* GET `/api/files/download/{fileId}`
* DELETE `/api/files/{fileId}`

### Temporary Download URL

* GET `/api/files/{fileId}/generate-url`
* GET `/api/files/download?token={token}`

## Security Features

* JWT-based Authentication
* AES Encryption for File Storage
* Temporary Download Tokens
* Upload Rate Limiting
* Audit Logging
* Monitoring with Spring Actuator

## Running the Application

### Clone Repository

git clone <repository-url>

### Build Project

mvn clean install

### Run Application

mvn spring-boot:run


## Author

Mahesh Kapilavai

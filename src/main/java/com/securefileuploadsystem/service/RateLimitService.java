package com.securefileuploadsystem.service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Service
public class RateLimitService {

	private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

	public Bucket resolveBucket(String username) {
	    return buckets.computeIfAbsent(
	            username,
	            this::newBucket);
	}

	private Bucket newBucket(String username) {

	    return Bucket.builder()
	            .addLimit(
	                    Bandwidth.builder()
	                            .capacity(2)
	                            .refillGreedy(
	                                    2,
	                                    Duration.ofMinutes(1))
	                            .build())
	            .build();
	}
}

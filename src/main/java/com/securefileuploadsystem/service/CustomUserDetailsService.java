package com.securefileuploadsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.securefileuploadsystem.entity.User;
import com.securefileuploadsystem.repository.UserRepository;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails
    loadUserByUsername(
            String username) {

        User user = userRepo
                .findByUsername(username)
                .orElseThrow();

        return org.springframework.security
                .core.userdetails.User
                .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}

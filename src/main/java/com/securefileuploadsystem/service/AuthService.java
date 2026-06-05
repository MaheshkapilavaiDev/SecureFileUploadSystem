package com.securefileuploadsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.securefileuploadsystem.dto.LoginRequest;
import com.securefileuploadsystem.dto.RegisterRequest;
import com.securefileuploadsystem.entity.User;
import com.securefileuploadsystem.repository.UserRepository;
import com.securefileuploadsystem.security.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;
    

    public User register(
            RegisterRequest request) {

        User user = new User();

        user.setUsername(
                request.getUsername());

        user.setPassword(
                encoder.encode(
                        request.getPassword()));

        user.setRole(
                request.getRole());

        return userRepo.save(user);
    }

    public String login(
            LoginRequest request) {

        User user =
                userRepo.findByUsername(
                        request.getUsername())
                .orElseThrow();

        if(!encoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid Credentials");
        }

        return jwtUtil.generateToken(
                user.getUsername());
    }
}

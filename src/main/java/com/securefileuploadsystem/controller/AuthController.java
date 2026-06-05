package com.securefileuploadsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.securefileuploadsystem.dto.LoginRequest;
import com.securefileuploadsystem.dto.RegisterRequest;
import com.securefileuploadsystem.entity.User;
import com.securefileuploadsystem.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public User register(
            @RequestBody
            RegisterRequest request) {

        return authService
                .register(request);
    }

    @PostMapping("/login")
    public String login(
            @RequestBody
            LoginRequest request) {

        return authService
                .login(request);
    }
}

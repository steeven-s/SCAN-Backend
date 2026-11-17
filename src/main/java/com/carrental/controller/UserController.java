package com.carrental.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrental.dto.LoginRequest;
import com.carrental.dto.LoginResponse;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.RegisterResponse;
import com.carrental.service.AuthService;
import com.carrental.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://scancars.vercel.app")
public class UserController {
	
    private final UserService userService;
    private final AuthService authService;
    public UserController(
            UserService userService,
            AuthService authService) {

        this.userService = userService;
        this.authService = authService;
    }
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response =
                userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }	
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt) {

        return Map.of(
                "userId", jwt.getClaim("userId"),
                "email", jwt.getSubject(),
                "name", jwt.getClaim("name"),
                "role", jwt.getClaim("role")
        );
    }
}

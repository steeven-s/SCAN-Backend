package com.carrental.service;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.carrental.dto.RegisterRequest;
import com.carrental.dto.RegisterResponse;
import com.carrental.entity.Role;
import com.carrental.entity.User;
import com.carrental.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Check whether email already exists
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        // Create new user
        User user = new User();

        user.setFullName(request.getFullName().trim());
        user.setEmail(email);

        // Encrypt password before saving
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setPhone(
                request.getPhone() == null
                        ? null
                        : request.getPhone().trim()
        );

        // Public registration always creates CUSTOMER
        user.setRole(Role.CUSTOMER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }
}
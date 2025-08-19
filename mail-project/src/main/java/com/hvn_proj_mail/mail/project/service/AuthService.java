package com.hvn_proj_mail.mail.project.service;

import com.hvn_proj_mail.mail.project.entity.User;
import com.hvn_proj_mail.mail.project.repository.UserRepository;
import com.hvn_proj_mail.mail.project.request.AuthResponse;
import com.hvn_proj_mail.mail.project.request.LoginRequest;
import com.hvn_proj_mail.mail.project.request.RegisterRequest;
import com.hvn_proj_mail.mail.project.response.LaoIdResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, "Registration successful");
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = jwtService.generateToken(request.getEmail());
        return new AuthResponse(token, "Login successful");
    }

    public AuthResponse saveLaoIdUser(LaoIdResponse.Data laoIdUser) {
        // Lấy email từ array emails (lấy email đầu tiên)
        String email = null;
        if (laoIdUser.getEmails() != null && laoIdUser.getEmails().length > 0) {
            email = laoIdUser.getEmails()[0]; // Lấy email đầu tiên
        }

        System.out.println("Email from LaoID: " + email);

        Optional<User> existingUser = email != null ? userRepository.findByEmail(email) : Optional.empty();
        System.out.println("Existing user by email: " + existingUser.isPresent());

        if (existingUser.isEmpty()) {
            existingUser = userRepository.findByLaoidId(laoIdUser.getId());
            System.out.println("Existing user by LaoID ID: " + existingUser.isPresent());
        }

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setName(laoIdUser.getFirstName() + " " + laoIdUser.getLastName());
            user.setEmail(email);
            user.setLaoidId(laoIdUser.getId());
            System.out.println("Updated existing user: " + user);
        } else {
            user = User.builder()
                    .name(laoIdUser.getFirstName() + " " + laoIdUser.getLastName())
                    .email(email)
                    .password(null) // No password for LaoID users
                    .role("USER")
                    .laoidId(laoIdUser.getId())
                    .build();
            System.out.println("Created new user: " + user);
        }

        userRepository.save(user);
        System.out.println("User saved to database");
        System.out.println("User email after save: " + user.getEmail());

        System.out.println("Calling jwtService.generateToken...");
        String token = jwtService.generateToken(user.getEmail());
        System.out.println("Token returned: " + token);
        System.out.println("Token is null: " + (token == null));

        AuthResponse response = new AuthResponse(token, "LaoID login successful");
        System.out.println("AuthResponse created: " + response);
        System.out.println("AuthResponse token: " + response.getToken());
        System.out.println("=== END DEBUG ===");

        return response;
    }
}
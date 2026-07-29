package com.connectx.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.connectx.entity.User;
import com.connectx.enums.OnlineStatus;
import com.connectx.repository.UserRepository;
import com.connectx.security.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(LoginRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(request.getUsername());

        return new LoginResponseDTO(
                user.getId(),
                user.getUsername(),
                token
        );
    }
    
 
    public String register(RegisterRequestDTO request) {

    	if (userRepository.findByUsername(request.getUsername()).isPresent()) {
    	    return "Username already exists. Please choose a different username.";
    	}

    	if (userRepository.findByEmail(request.getEmail()).isPresent()) {
    	    return "Email already registered. Please use a different email.";
    	}

        User user = User.builder()
                .fullName(request.getFullName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .onlineStatus(OnlineStatus.OFFLINE)
                .build();

        userRepository.save(user);

        return "User Registered Successfully";
    }
}
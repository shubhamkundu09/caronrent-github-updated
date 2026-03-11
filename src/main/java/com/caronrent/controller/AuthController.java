package com.caronrent.controller;

import com.caronrent.dto.ApiResponse;
import com.caronrent.dto.JwtResponse;
import com.caronrent.dto.LoginRequest;
import com.caronrent.dto.SignupRequest;
import com.caronrent.dto.VerifyOTPRequest;
import com.caronrent.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody SignupRequest request) {
        String response = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", response));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody VerifyOTPRequest request) {
        String response = authService.verifyOtpAndRegisterWithPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        JwtResponse jwtResponse = new JwtResponse(token, "Bearer", request.getEmail(), true);
        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
    }

    @GetMapping("/public/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Auth Service is running", "OK"));
    }
}
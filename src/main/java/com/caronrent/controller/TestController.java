package com.caronrent.controller;

import com.caronrent.dto.ApiResponse;
import com.caronrent.service.IdEncryptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final IdEncryptionService idEncryptionService;

    public TestController(IdEncryptionService idEncryptionService) {
        this.idEncryptionService = idEncryptionService;
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> publicEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("Public endpoint accessed", "This is a public endpoint"));
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> userEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("User endpoint accessed", "This is a user endpoint"));
    }

    @GetMapping("/carowner")
    @PreAuthorize("hasAnyRole('CAROWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> carOwnerEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("Car owner endpoint accessed", "This is a car owner endpoint"));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> adminEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("Admin endpoint accessed", "This is an admin endpoint"));
    }

    @GetMapping("/my-info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyInfo(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication != null) {
            response.put("username", authentication.getName());
            response.put("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            response.put("isAuthenticated", authentication.isAuthenticated());
            return ResponseEntity.ok(ApiResponse.success("User info retrieved", response));
        } else {
            return ResponseEntity.ok(ApiResponse.success("Not authenticated", response));
        }
    }

    @GetMapping("/encrypt/{id}")
    public ResponseEntity<ApiResponse<String>> encryptId(@PathVariable Long id) {
        String encrypted = idEncryptionService.encryptId(id);
        return ResponseEntity.ok(ApiResponse.success("ID encrypted successfully", encrypted));
    }

    @GetMapping("/decrypt/{encryptedId}")
    public ResponseEntity<ApiResponse<Long>> decryptId(@PathVariable String encryptedId) {
        Long decrypted = idEncryptionService.decryptId(encryptedId);
        return ResponseEntity.ok(ApiResponse.success("ID decrypted successfully", decrypted));
    }
}
package com.caronrent.controller;

import com.caronrent.dto.ApiResponse;
import com.caronrent.service.IdEncryptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    private final IdEncryptionService idEncryptionService;

    public DebugController(IdEncryptionService idEncryptionService) {
        this.idEncryptionService = idEncryptionService;
    }

    @GetMapping("/encrypt/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> encryptId(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            String encrypted = idEncryptionService.encryptId(id);
            response.put("original", id);
            response.put("encrypted", encrypted);
            return ResponseEntity.ok(ApiResponse.success("ID encrypted successfully", response));
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage());
        }
    }

    @GetMapping("/decrypt/{encryptedId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> decryptId(@PathVariable String encryptedId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long decrypted = idEncryptionService.decryptId(encryptedId);
            response.put("encrypted", encryptedId);
            response.put("decrypted", decrypted);
            return ResponseEntity.ok(ApiResponse.success("ID decrypted successfully", response));
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage());
        }
    }

    @GetMapping("/test-encryption")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testEncryption() {
        Map<String, Object> response = new HashMap<>();
        try {
            Long testId = 123L;
            String encrypted = idEncryptionService.encryptId(testId);
            Long decrypted = idEncryptionService.decryptId(encrypted);

            response.put("testId", testId);
            response.put("encrypted", encrypted);
            response.put("decrypted", decrypted);
            response.put("matches", testId.equals(decrypted));

            return ResponseEntity.ok(ApiResponse.success("Encryption test completed", response));
        } catch (Exception e) {
            throw new RuntimeException("Encryption test failed: " + e.getMessage());
        }
    }
}
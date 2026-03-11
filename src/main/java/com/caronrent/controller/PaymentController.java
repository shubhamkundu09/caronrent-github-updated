package com.caronrent.controller;

import com.caronrent.dto.ApiResponse;
import com.caronrent.dto.CreatePaymentRequest;
import com.caronrent.dto.PaymentResponse;
import com.caronrent.dto.PaymentVerificationRequest;
import com.caronrent.entity.Booking;
import com.caronrent.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPaymentOrder(
            @RequestBody CreatePaymentRequest request,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            System.out.println("💰 Creating payment order for user: " + userEmail);

            PaymentResponse response = paymentService.createPaymentOrder(request);
            return ResponseEntity.ok(ApiResponse.success("Payment order created successfully", response));
        } catch (RazorpayException e) {
            System.err.println("❌ Razorpay error: " + e.getMessage());
            throw new RuntimeException("Payment order creation failed: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyPayment(
            @RequestBody PaymentVerificationRequest request,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Booking booking = paymentService.verifyPayment(request);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("booking", booking);
            responseData.put("paymentDetails", Map.of(
                    "paymentId", booking.getPaymentId(),
                    "orderId", booking.getOrderId(),
                    "amountPaid", booking.getAmountPaid(),
                    "paymentStatus", booking.getPaymentStatus()
            ));

            return ResponseEntity.ok(ApiResponse.success("Payment verified successfully", responseData));
        } catch (Exception e) {
            System.err.println("❌ Payment verification failed: " + e.getMessage());
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }

    @GetMapping("/status/{encryptedBookingId}")
    @PreAuthorize("hasAnyRole('USER', 'CAROWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPaymentStatus(
            @PathVariable String encryptedBookingId,
            Authentication authentication) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("bookingId", encryptedBookingId);
            response.put("message", "Payment status endpoint - implement this method");

            return ResponseEntity.ok(ApiResponse.success("Payment status retrieved", response));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get payment status: " + e.getMessage());
        }
    }

    @PostMapping("/generate-signature")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateSignature(
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            String orderId = request.get("orderId");
            String paymentId = request.get("paymentId");

            if (orderId == null || orderId.isEmpty() || paymentId == null || paymentId.isEmpty()) {
                throw new RuntimeException("Both orderId and paymentId are required");
            }

            String data = orderId + "|" + paymentId;
            String generatedSignature = calculateHMAC(data, razorpayKeySecret);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("paymentId", paymentId);
            response.put("signature", generatedSignature);
            response.put("data", data);

            return ResponseEntity.ok(ApiResponse.success("Signature generated successfully", response));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature: " + e.getMessage());
        }
    }

    private String calculateHMAC(String data, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
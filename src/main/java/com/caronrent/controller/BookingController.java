package com.caronrent.controller;

import com.caronrent.dto.ApiResponse;
import com.caronrent.dto.BookingRequestDTO;
import com.caronrent.dto.BookingResponseDTO;
import com.caronrent.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/user/create")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @RequestBody BookingRequestDTO bookingRequest,
            Authentication authentication) {
        String email = authentication.getName();
        BookingResponseDTO booking = bookingService.createBooking(email, bookingRequest);
        return ResponseEntity.ok(ApiResponse.success("Booking created successfully. Please complete payment.", booking));
    }

    @GetMapping("/user/my-bookings")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getMyBookings(Authentication authentication) {
        String email = authentication.getName();
        List<BookingResponseDTO> bookings = bookingService.getUserBookings(email);
        return ResponseEntity.ok(ApiResponse.success("Bookings retrieved successfully", bookings));
    }

    @PutMapping("/user/{encryptedBookingId}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBooking(
            @PathVariable String encryptedBookingId,
            Authentication authentication) {
        String email = authentication.getName();
        BookingResponseDTO booking = bookingService.cancelBooking(encryptedBookingId, email);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully", booking));
    }

    @GetMapping("/owner/bookings")
    @PreAuthorize("hasAnyRole('CAROWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getOwnerBookings(Authentication authentication) {
        String email = authentication.getName();
        List<BookingResponseDTO> bookings = bookingService.getOwnerBookings(email);
        return ResponseEntity.ok(ApiResponse.success("Owner bookings retrieved successfully", bookings));
    }

    @PutMapping("/owner/{encryptedBookingId}/confirm")
    @PreAuthorize("hasAnyRole('CAROWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> confirmBooking(
            @PathVariable String encryptedBookingId,
            Authentication authentication) {
        String email = authentication.getName();
        BookingResponseDTO booking = bookingService.confirmBooking(encryptedBookingId, email);
        return ResponseEntity.ok(ApiResponse.success("Booking confirmed successfully", booking));
    }

    @PutMapping("/owner/{encryptedBookingId}/cancel")
    @PreAuthorize("hasAnyRole('CAROWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBookingByOwner(
            @PathVariable String encryptedBookingId,
            Authentication authentication) {
        String email = authentication.getName();
        BookingResponseDTO booking = bookingService.cancelBookingByOwner(encryptedBookingId, email);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully by owner", booking));
    }

    @PutMapping("/admin/{encryptedBookingId}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updatePaymentStatus(
            @PathVariable String encryptedBookingId,
            @RequestParam String status) {
        BookingResponseDTO booking = bookingService.updatePaymentStatus(encryptedBookingId, status);
        return ResponseEntity.ok(ApiResponse.success("Payment status updated successfully", booking));
    }

    @GetMapping("/{encryptedBookingId}")
    @PreAuthorize("hasAnyRole('USER', 'CAROWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingById(@PathVariable String encryptedBookingId) {
        BookingResponseDTO booking = bookingService.getBookingById(encryptedBookingId);
        return ResponseEntity.ok(ApiResponse.success("Booking retrieved successfully", booking));
    }
}
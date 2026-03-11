package com.caronrent.controller;

import com.caronrent.dto.ApiResponse;
import com.caronrent.dto.CarDTO;
import com.caronrent.dto.CarResponseDTO;
import com.caronrent.dto.CarStatusDTO;
import com.caronrent.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/owner/add")
    @PreAuthorize("hasAnyRole('CAROWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CarResponseDTO>> addCar(@RequestBody CarDTO carDTO, Authentication authentication) {
        String email = authentication.getName();
        CarResponseDTO car = carService.addCar(email, carDTO);
        return ResponseEntity.ok(ApiResponse.success("Car added successfully", car));
    }

    @GetMapping("/owner/my-cars")
    @PreAuthorize("hasAnyRole('CAROWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CarResponseDTO>>> getMyCars(Authentication authentication) {
        String email = authentication.getName();
        List<CarResponseDTO> cars = carService.getMyCars(email);
        return ResponseEntity.ok(ApiResponse.success("Your cars retrieved successfully", cars));
    }

    @PutMapping("/owner/{encryptedCarId}/status")
    @PreAuthorize("hasAnyRole('CAROWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CarResponseDTO>> updateCarStatus(
            @PathVariable String encryptedCarId,
            @RequestBody CarStatusDTO statusDTO,
            Authentication authentication) {
        String email = authentication.getName();
        CarResponseDTO car = carService.updateCarStatus(encryptedCarId, email, statusDTO);
        return ResponseEntity.ok(ApiResponse.success("Car status updated successfully", car));
    }

    @DeleteMapping("/owner/{encryptedCarId}")
    @PreAuthorize("hasAnyRole('CAROWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteCar(
            @PathVariable String encryptedCarId,
            Authentication authentication) {
        String email = authentication.getName();
        carService.deleteCar(encryptedCarId, email);
        return ResponseEntity.ok(ApiResponse.success("Car deleted successfully", "Car ID: " + encryptedCarId));
    }

    @GetMapping("/public/all")
    public ResponseEntity<ApiResponse<List<CarResponseDTO>>> getAllAvailableCars() {
        List<CarResponseDTO> cars = carService.getAllAvailableCars();
        return ResponseEntity.ok(ApiResponse.success("Available cars retrieved successfully", cars));
    }

    @GetMapping("/public/available")
    public ResponseEntity<ApiResponse<List<CarResponseDTO>>> getAvailableCarsByDate(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);

            List<CarResponseDTO> cars = carService.getAvailableCarsByDate(start, end);
            return ResponseEntity.ok(ApiResponse.success("Available cars for selected dates retrieved successfully", cars));
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Use ISO format: yyyy-MM-dd'T'HH:mm:ss");
        }
    }

    @GetMapping("/public/{encryptedCarId}")
    public ResponseEntity<ApiResponse<CarResponseDTO>> getCarById(@PathVariable String encryptedCarId) {
        CarResponseDTO car = carService.getCarById(encryptedCarId);
        return ResponseEntity.ok(ApiResponse.success("Car details retrieved successfully", car));
    }

    @GetMapping("/public/search/location")
    public ResponseEntity<ApiResponse<List<CarResponseDTO>>> searchByLocation(
            @RequestParam String location,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<CarResponseDTO> cars;

        if (startDate != null && endDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                LocalDateTime start = LocalDateTime.parse(startDate, formatter);
                LocalDateTime end = LocalDateTime.parse(endDate, formatter);
                cars = carService.searchCarsByLocationAndDate(location, start, end);
                return ResponseEntity.ok(ApiResponse.success("Cars found at location for selected dates", cars));
            } catch (Exception e) {
                throw new RuntimeException("Invalid date format. Use ISO format: yyyy-MM-dd'T'HH:mm:ss");
            }
        } else {
            cars = carService.searchCarsByLocation(location);
            return ResponseEntity.ok(ApiResponse.success("Cars found at location", cars));
        }
    }

    @GetMapping("/public/search/brand")
    public ResponseEntity<ApiResponse<List<CarResponseDTO>>> searchByBrand(
            @RequestParam String brand,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<CarResponseDTO> cars;

        if (startDate != null && endDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                LocalDateTime start = LocalDateTime.parse(startDate, formatter);
                LocalDateTime end = LocalDateTime.parse(endDate, formatter);
                cars = carService.searchCarsByBrandAndDate(brand, start, end);
                return ResponseEntity.ok(ApiResponse.success("Cars found for brand with selected dates", cars));
            } catch (Exception e) {
                throw new RuntimeException("Invalid date format. Use ISO format: yyyy-MM-dd'T'HH:mm:ss");
            }
        } else {
            cars = carService.searchCarsByBrand(brand);
            return ResponseEntity.ok(ApiResponse.success("Cars found for brand", cars));
        }
    }

    @GetMapping("/public/search/price")
    public ResponseEntity<ApiResponse<List<CarResponseDTO>>> searchByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        List<CarResponseDTO> cars;

        if (startDate != null && endDate != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                LocalDateTime start = LocalDateTime.parse(startDate, formatter);
                LocalDateTime end = LocalDateTime.parse(endDate, formatter);
                cars = carService.searchCarsByPriceRangeAndDate(minPrice, maxPrice, start, end);
                return ResponseEntity.ok(ApiResponse.success("Cars found in price range for selected dates", cars));
            } catch (Exception e) {
                throw new RuntimeException("Invalid date format. Use ISO format: yyyy-MM-dd'T'HH:mm:ss");
            }
        } else {
            cars = carService.searchCarsByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(ApiResponse.success("Cars found in price range", cars));
        }
    }
}
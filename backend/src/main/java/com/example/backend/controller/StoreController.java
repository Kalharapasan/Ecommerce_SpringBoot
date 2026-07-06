package com.example.backend.controller;

import com.example.backend.config.SecurityConfig;
import com.example.backend.dto.StoreDto;
import com.example.backend.dto.StoreReviewDto;
import com.example.backend.dto.custom.JwtDataDto;
import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.service.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;
    private final SecurityConfig securityConfig;

    public StoreController(StoreService storeService, SecurityConfig securityConfig) {
        this.storeService = storeService;
        this.securityConfig = securityConfig;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> registerStore(@RequestBody StoreDto dto, @RequestParam String token) {
        ResponseDto response = new ResponseDto();
        if (token == null || token.isEmpty()) {
            response.setMessage("Authentication token is required");
            return ResponseEntity.badRequest().body(response);
        }
        JwtDataDto jwtData = securityConfig.getJWTData(token);
        if (jwtData.getUserId() == null) {
            response.setMessage("User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }
        return storeService.registerStore(dto, jwtData.getUserId());
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateStore(@RequestBody StoreDto dto, @RequestParam String token) {
        ResponseDto response = new ResponseDto();
        if (token == null || token.isEmpty()) {
            response.setMessage("Authentication token is required");
            return ResponseEntity.badRequest().body(response);
        }
        JwtDataDto jwtData = securityConfig.getJWTData(token);
        if (jwtData.getUserId() == null) {
            response.setMessage("User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }
        // Protect endpoint: user must be SELLER or ADMIN
        if (!"SELLER".equals(jwtData.getRole()) && !"ADMIN".equals(jwtData.getRole())) {
            response.setMessage("Access denied. Sellers or Admins only.");
            return ResponseEntity.status(403).body(response);
        }
        return storeService.updateStore(dto, jwtData.getUserId());
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<ResponseDto> getStoreById(@PathVariable Integer storeId) {
        return storeService.getStoreById(storeId);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDto> getStoreByOwnerId(@PathVariable Integer userId) {
        return storeService.getStoreByOwnerId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDto> getAllStores() {
        return storeService.getAllStores();
    }

    @PostMapping("/{storeId}/follow")
    public ResponseEntity<ResponseDto> toggleFollowStore(@PathVariable Integer storeId, @RequestParam String token) {
        ResponseDto response = new ResponseDto();
        if (token == null || token.isEmpty()) {
            response.setMessage("Authentication token is required");
            return ResponseEntity.badRequest().body(response);
        }
        JwtDataDto jwtData = securityConfig.getJWTData(token);
        if (jwtData.getUserId() == null) {
            response.setMessage("User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }
        return storeService.toggleFollowStore(storeId, jwtData.getUserId());
    }

    @GetMapping("/{storeId}/is-following")
    public ResponseEntity<ResponseDto> isFollowing(@PathVariable Integer storeId, @RequestParam String token) {
        ResponseDto response = new ResponseDto();
        if (token == null || token.isEmpty()) {
            response.setMessage("Authentication token is required");
            return ResponseEntity.badRequest().body(response);
        }
        JwtDataDto jwtData = securityConfig.getJWTData(token);
        if (jwtData.getUserId() == null) {
            response.setMessage("User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }
        return storeService.isFollowing(storeId, jwtData.getUserId());
    }

    @PostMapping("/{storeId}/review")
    public ResponseEntity<ResponseDto> submitReview(@PathVariable Integer storeId,
                                                    @RequestBody StoreReviewDto dto,
                                                    @RequestParam String token) {
        ResponseDto response = new ResponseDto();
        if (token == null || token.isEmpty()) {
            response.setMessage("Authentication token is required");
            return ResponseEntity.badRequest().body(response);
        }
        JwtDataDto jwtData = securityConfig.getJWTData(token);
        if (jwtData.getUserId() == null) {
            response.setMessage("User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }
        dto.setStoreId(storeId);
        return storeService.submitReview(dto, jwtData.getUserId());
    }

    @GetMapping("/{storeId}/reviews")
    public ResponseEntity<ResponseDto> getReviews(@PathVariable Integer storeId) {
        return storeService.getReviews(storeId);
    }

    @GetMapping("/{storeId}/products")
    public ResponseEntity<ResponseDto> getStoreProducts(@PathVariable Integer storeId,
                                                         @RequestParam(required = false) String search) {
        return storeService.getStoreProducts(storeId, search);
    }

    @GetMapping("/analytics")
    public ResponseEntity<ResponseDto> getAnalytics(@RequestParam String token) {
        ResponseDto response = new ResponseDto();
        if (token == null || token.isEmpty()) {
            response.setMessage("Authentication token is required");
            return ResponseEntity.badRequest().body(response);
        }
        JwtDataDto jwtData = securityConfig.getJWTData(token);
        if (jwtData.getUserId() == null) {
            response.setMessage("User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }
        if (!"SELLER".equals(jwtData.getRole()) && !"ADMIN".equals(jwtData.getRole())) {
            response.setMessage("Access denied. Sellers or Admins only.");
            return ResponseEntity.status(403).body(response);
        }
        return storeService.getAnalytics(jwtData.getUserId());
    }

    @PutMapping("/{storeId}/verify")
    public ResponseEntity<ResponseDto> toggleVerifyStore(@PathVariable Integer storeId, @RequestParam String token) {
        ResponseDto response = new ResponseDto();
        if (token == null || token.isEmpty()) {
            response.setMessage("Authentication token is required");
            return ResponseEntity.badRequest().body(response);
        }
        JwtDataDto jwtData = securityConfig.getJWTData(token);
        if (jwtData.getUserId() == null) {
            response.setMessage("User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }
        // Protect endpoint: Admins only
        if (!"ADMIN".equals(jwtData.getRole())) {
            response.setMessage("Access denied. Admins only.");
            return ResponseEntity.status(403).body(response);
        }
        return storeService.toggleVerifyStore(storeId);
    }
}

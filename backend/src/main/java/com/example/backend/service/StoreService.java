package com.example.backend.service;

import com.example.backend.dto.ProductDto;
import com.example.backend.dto.StoreDto;
import com.example.backend.dto.StoreReviewDto;
import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.http.ResponseEntity;
import com.example.backend.config.SecurityConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StoreService {

    private final StoreRepo storeRepo;
    private final StoreFollowerRepo storeFollowerRepo;
    private final StoreReviewRepo storeReviewRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final OrderItemRepo orderItemRepo;
    private final SecurityConfig securityConfig;

    public StoreService(StoreRepo storeRepo,
                        StoreFollowerRepo storeFollowerRepo,
                        StoreReviewRepo storeReviewRepo,
                        UserRepo userRepo,
                        ProductRepo productRepo,
                        OrderItemRepo orderItemRepo,
                        SecurityConfig securityConfig) {
        this.storeRepo = storeRepo;
        this.storeFollowerRepo = storeFollowerRepo;
        this.storeReviewRepo = storeReviewRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.orderItemRepo = orderItemRepo;
        this.securityConfig = securityConfig;
    }

    @Transactional
    public ResponseEntity<ResponseDto> registerStore(StoreDto dto, Integer userId) {
        ResponseDto response = new ResponseDto();
        Optional<User> optUser = userRepo.findById(userId);
        if (!optUser.isPresent()) {
            response.setMessage("User not found");
            return ResponseEntity.badRequest().body(response);
        }

        User user = optUser.get();
        Optional<Store> existingStore = storeRepo.findByOwnerUserId(userId);
        if (existingStore.isPresent()) {
            response.setMessage("User already has a registered store");
            response.setData(convertToDto(existingStore.get()));
            return ResponseEntity.badRequest().body(response);
        }

        // Change user role to SELLER
        user.setRole("SELLER");
        userRepo.save(user);

        Store store = new Store();
        store.setOwner(user);
        store.setStoreName(dto.getStoreName() != null ? dto.getStoreName() : user.getUsername() + "'s Store");
        store.setStoreDescription(dto.getStoreDescription());
        store.setStoreLogoUrl(dto.getStoreLogoUrl());
        store.setStoreBannerUrl(dto.getStoreBannerUrl());
        store.setStorePolicies(dto.getStorePolicies());
        store.setIsVerified(false);
        store.setFollowersCount(0);
        store.setRating(BigDecimal.ZERO);
        store.setReviewsCount(0);
        store.setCreatedDate(new Date());

        Store savedStore = storeRepo.save(store);

        String newToken = securityConfig.generateToken(user.getUsername(), user.getUserId(), "SELLER");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("store", convertToDto(savedStore));
        dataMap.put("token", newToken);

        response.setMessage("Store registered successfully");
        response.setData(dataMap);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ResponseDto> updateStore(StoreDto dto, Integer userId) {
        ResponseDto response = new ResponseDto();
        Optional<Store> optStore = storeRepo.findByOwnerUserId(userId);
        if (!optStore.isPresent()) {
            response.setMessage("Store not found for this user");
            return ResponseEntity.badRequest().body(response);
        }

        Store store = optStore.get();
        if (dto.getStoreName() != null && !dto.getStoreName().trim().isEmpty()) {
            store.setStoreName(dto.getStoreName());
        }
        store.setStoreDescription(dto.getStoreDescription());
        store.setStoreLogoUrl(dto.getStoreLogoUrl());
        store.setStoreBannerUrl(dto.getStoreBannerUrl());
        store.setStorePolicies(dto.getStorePolicies());

        Store updatedStore = storeRepo.save(store);
        response.setMessage("Store updated successfully");
        response.setData(convertToDto(updatedStore));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ResponseDto> getStoreById(Integer storeId) {
        ResponseDto response = new ResponseDto();
        Optional<Store> optStore = storeRepo.findById(storeId);
        if (!optStore.isPresent()) {
            response.setMessage("Store not found");
            return ResponseEntity.badRequest().body(response);
        }
        response.setMessage("Store retrieved successfully");
        response.setData(convertToDto(optStore.get()));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ResponseDto> getStoreByOwnerId(Integer ownerId) {
        ResponseDto response = new ResponseDto();
        Optional<Store> optStore = storeRepo.findByOwnerUserId(ownerId);
        if (!optStore.isPresent()) {
            response.setMessage("Store not found for owner ID: " + ownerId);
            return ResponseEntity.badRequest().body(response);
        }
        response.setMessage("Store retrieved successfully");
        response.setData(convertToDto(optStore.get()));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ResponseDto> getAllStores() {
        ResponseDto response = new ResponseDto();
        List<StoreDto> dtoList = storeRepo.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        response.setMessage("Stores retrieved successfully");
        response.setData(dtoList);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ResponseDto> toggleFollowStore(Integer storeId, Integer userId) {
        ResponseDto response = new ResponseDto();
        Optional<Store> optStore = storeRepo.findById(storeId);
        if (!optStore.isPresent()) {
            response.setMessage("Store not found");
            return ResponseEntity.badRequest().body(response);
        }
        Optional<User> optUser = userRepo.findById(userId);
        if (!optUser.isPresent()) {
            response.setMessage("User not found");
            return ResponseEntity.badRequest().body(response);
        }

        Store store = optStore.get();
        User user = optUser.get();

        Optional<StoreFollower> existing = storeFollowerRepo.findByUserUserIdAndStoreStoreId(userId, storeId);
        if (existing.isPresent()) {
            storeFollowerRepo.delete(existing.get());
            int newCount = Math.max(0, store.getFollowersCount() - 1);
            store.setFollowersCount(newCount);
            storeRepo.save(store);
            response.setMessage("Unfollowed store successfully");
            response.setData(false); // isFollowing = false
        } else {
            StoreFollower follower = new StoreFollower();
            follower.setStore(store);
            follower.setUser(user);
            follower.setFollowedDate(new Date());
            storeFollowerRepo.save(follower);

            store.setFollowersCount(store.getFollowersCount() + 1);
            storeRepo.save(store);
            response.setMessage("Followed store successfully");
            response.setData(true); // isFollowing = true
        }

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ResponseDto> isFollowing(Integer storeId, Integer userId) {
        ResponseDto response = new ResponseDto();
        boolean isFollowing = storeFollowerRepo.existsByUserUserIdAndStoreStoreId(userId, storeId);
        response.setMessage("Checked follow status");
        response.setData(isFollowing);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ResponseDto> submitReview(StoreReviewDto dto, Integer userId) {
        ResponseDto response = new ResponseDto();
        Optional<Store> optStore = storeRepo.findById(dto.getStoreId());
        if (!optStore.isPresent()) {
            response.setMessage("Store not found");
            return ResponseEntity.badRequest().body(response);
        }
        Optional<User> optUser = userRepo.findById(userId);
        if (!optUser.isPresent()) {
            response.setMessage("User not found");
            return ResponseEntity.badRequest().body(response);
        }

        Store store = optStore.get();
        User user = optUser.get();

        StoreReview review = new StoreReview();
        review.setStore(store);
        review.setUser(user);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setCreatedDate(new Date());

        storeReviewRepo.save(review);

        // Recalculate average rating
        Double avgRating = storeReviewRepo.getAverageRatingForStore(store.getStoreId());
        if (avgRating != null) {
            store.setRating(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
        } else {
            store.setRating(BigDecimal.ZERO);
        }

        // Increment reviews count
        store.setReviewsCount(store.getReviewsCount() + 1);
        storeRepo.save(store);

        response.setMessage("Review submitted successfully");
        response.setData(convertToReviewDto(review));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ResponseDto> getReviews(Integer storeId) {
        ResponseDto response = new ResponseDto();
        List<StoreReviewDto> reviews = storeReviewRepo.findByStoreStoreIdOrderByCreatedDateDesc(storeId).stream()
                .map(this::convertToReviewDto)
                .collect(Collectors.toList());
        response.setMessage("Reviews retrieved successfully");
        response.setData(reviews);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<ResponseDto> toggleVerifyStore(Integer storeId) {
        ResponseDto response = new ResponseDto();
        Optional<Store> optStore = storeRepo.findById(storeId);
        if (!optStore.isPresent()) {
            response.setMessage("Store not found");
            return ResponseEntity.badRequest().body(response);
        }
        Store store = optStore.get();
        store.setIsVerified(!store.getIsVerified());
        Store saved = storeRepo.save(store);
        response.setMessage("Verification status toggled to: " + saved.getIsVerified());
        response.setData(convertToDto(saved));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ResponseDto> getAnalytics(Integer userId) {
        ResponseDto response = new ResponseDto();
        Optional<Store> optStore = storeRepo.findByOwnerUserId(userId);
        if (!optStore.isPresent()) {
            response.setMessage("Store not found for user");
            return ResponseEntity.badRequest().body(response);
        }

        Store store = optStore.get();
        Integer storeId = store.getStoreId();

        // 1. Total Products
        List<Product> products = productRepo.findByStoreStoreId(storeId);
        int totalProducts = products.size();

        // 2. Order Items
        List<OrderItem> orderItems = orderItemRepo.findByProductStoreStoreId(storeId);

        // 3. Analytics aggregation
        double totalRevenue = 0.0;
        Set<Integer> distinctOrderIds = new HashSet<>();
        for (OrderItem item : orderItems) {
            // Include only order items where the parent order is not cancelled
            Order order = item.getOrder();
            if (order != null && (order.getStatus() == null || !order.getStatus().equalsIgnoreCase("CANCELLED"))) {
                totalRevenue += item.getPrice() * item.getQuantity();
                distinctOrderIds.add(order.getOrderId());
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("storeId", storeId);
        stats.put("storeName", store.getStoreName());
        stats.put("storeLogoUrl", store.getStoreLogoUrl());
        stats.put("isVerified", store.getIsVerified());
        stats.put("totalRevenue", totalRevenue);
        stats.put("ordersCount", distinctOrderIds.size());
        stats.put("productsCount", totalProducts);
        stats.put("followersCount", store.getFollowersCount());
        stats.put("rating", store.getRating());
        stats.put("reviewsCount", store.getReviewsCount());

        response.setMessage("Analytics retrieved successfully");
        response.setData(stats);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<ResponseDto> getStoreProducts(Integer storeId, String search) {
        ResponseDto response = new ResponseDto();
        List<Product> products;
        if (search != null && !search.trim().isEmpty()) {
            products = productRepo.findByStoreStoreIdAndProductNameContainingIgnoreCase(storeId, search);
        } else {
            products = productRepo.findByStoreStoreId(storeId);
        }

        List<ProductDto> dtoList = products.stream()
                .map(product -> {
                    ProductDto dto = new ProductDto();
                    dto.setProductId(product.getProductId());
                    dto.setProductName(product.getProductName());
                    dto.setDescription(product.getDescription());
                    dto.setImageUrl(product.getImageUrl());
                    dto.setPrice(product.getPrice());
                    if (product.getCategory() != null) {
                        dto.setCategoryId(product.getCategory().getCategoryId());
                        dto.setCategoryName(product.getCategory().getCategoryName());
                    }
                    dto.setStock(product.getStock());
                    if (product.getStore() != null) {
                        dto.setStoreId(product.getStore().getStoreId());
                        dto.setStoreName(product.getStore().getStoreName());
                        dto.setStoreLogoUrl(product.getStore().getStoreLogoUrl());
                        dto.setStoreVerified(product.getStore().getIsVerified());
                        dto.setStoreRating(product.getStore().getRating());
                        dto.setStoreReviewsCount(product.getStore().getReviewsCount());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        response.setMessage("Products retrieved successfully");
        response.setData(dtoList);
        return ResponseEntity.ok(response);
    }

    private StoreDto convertToDto(Store store) {
        return new StoreDto(
                store.getStoreId(),
                store.getOwner().getUserId(),
                store.getOwner().getFullName() != null ? store.getOwner().getFullName() : store.getOwner().getUsername(),
                store.getStoreName(),
                store.getStoreDescription(),
                store.getStoreLogoUrl(),
                store.getStoreBannerUrl(),
                store.getStorePolicies(),
                store.getIsVerified(),
                store.getFollowersCount(),
                store.getRating(),
                store.getReviewsCount(),
                store.getCreatedDate()
        );
    }

    private StoreReviewDto convertToReviewDto(StoreReview review) {
        return new StoreReviewDto(
                review.getReviewId(),
                review.getStore().getStoreId(),
                review.getUser().getUserId(),
                review.getUser().getUsername(),
                review.getRating(),
                review.getComment(),
                review.getCreatedDate()
        );
    }
}

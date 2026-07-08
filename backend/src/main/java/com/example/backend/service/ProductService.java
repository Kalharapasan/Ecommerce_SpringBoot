package com.example.backend.service;
import com.example.backend.dto.ProductDto;
import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.dto.custom.JwtDataDto;
import com.example.backend.config.SecurityConfig;
import com.example.backend.model.Category;
import com.example.backend.model.Product;
import com.example.backend.repository.CategoryRepo;
import com.example.backend.repository.ProductRepo;
import com.example.backend.repository.StoreRepo;
import com.example.backend.model.Store;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Calendar;
import java.util.Date;

@Service
public class ProductService {

    private static final String MSG_CODE_1 = "SOME IMPORTANT FIELDS ARE NULL!!!";
    private static final String MSG_CODE_2 = "SOME IMPORTANT FIELDS ARE MISSING!!!";
    private static final String MSG_CODE_3 = "PRODUCT NAME ALREADY USED";
    private static final String MSG_CODE_4 = "PRODUCT CREATED SUCCESSFULLY";
    private static final String MSG_CODE_5 = "PRODUCT CREATION FAILED";
    private static final String MSG_CODE_6 = "PRODUCT NOT FOUND";
    private static final String MSG_CODE_7 = "PRODUCT FOUND";
    private static final String MSG_CODE_8 = "PRODUCT UPDATED";
    private static final String MSG_CODE_9 = "PRODUCT DELETE FAILED";
    private static final String MSG_CODE_10 = "PRODUCT DELETED";
    private static final String MSG_CODE_11 = "CATEGORY NOT FOUND";

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final StoreRepo storeRepo;
    private final SecurityConfig securityConfig;

    public ProductService(ProductRepo productRepo, CategoryRepo categoryRepo, StoreRepo storeRepo, SecurityConfig securityConfig) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.storeRepo = storeRepo;
        this.securityConfig = securityConfig;
    }

    public ResponseEntity createProduct(ProductDto product) {
        ResponseDto response = new ResponseDto();
        if (product.getProductName() == null || product.getImageUrl() == null || product.getDescription() == null || product.getPrice() == null || product.getCategoryId() == null) {
            return ResponseEntity.badRequest().body(MSG_CODE_1);
        }
        if (product.getProductName().equals("") || product.getDescription().equals("") || product.getImageUrl().equals("")) {
            return ResponseEntity.badRequest().body(MSG_CODE_2);
        }

        Optional<Category> optionalCategory = categoryRepo.findById(product.getCategoryId());
        if (!optionalCategory.isPresent()) {
            return ResponseEntity.badRequest().body(MSG_CODE_11);
        }

        Optional<Product> optProduct = productRepo.findByProductName(product.getProductName());
        if (optProduct.isPresent()) {
            return ResponseEntity.badRequest().body(MSG_CODE_3);
        }
        Product data = new Product();
        data.setProductName(product.getProductName());
        data.setDescription(product.getDescription());
        data.setImageUrl(product.getImageUrl());
        data.setPrice(product.getPrice());
        data.setCategory(categoryRepo.findById(product.getCategoryId()).get());
        data.setStock(product.getStock() != null ? product.getStock() : 0);
        data.setConditionType(product.getConditionType() != null ? product.getConditionType() : "Brand New");
        data.setIsAuction(product.getIsAuction() != null ? product.getIsAuction() : false);
        if (Boolean.TRUE.equals(product.getIsAuction())) {
            data.setCurrentBid(product.getPrice());
            data.setBidsCount(0);
            if (product.getAuctionEndDate() != null) {
                data.setAuctionEndDate(product.getAuctionEndDate());
            } else {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, 7);
                data.setAuctionEndDate(cal.getTime());
            }
            data.setStock(1); // Auctions are usually single item
        }
        if (product.getStoreId() != null) {
            Optional<Store> store = storeRepo.findById(product.getStoreId());
            store.ifPresent(data::setStore);
        }

        Product savedProduct = productRepo.save(data);
        if (savedProduct.getProductId() != null) {
            response.setMessage(MSG_CODE_4);
            response.setData(convertToDto(savedProduct));
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.badRequest().body(MSG_CODE_5);
        }
    }

    public ResponseEntity getProductList() {
        ResponseDto response = new ResponseDto();
        List<Product> productList = productRepo.findAll();
        List<ProductDto> dtoList = productList.stream()
                .map(this::convertToDto)
                .collect(java.util.stream.Collectors.toList());
        response.setMessage(MSG_CODE_7);
        response.setData(dtoList);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity getProductById(Integer productId) {
        ResponseDto response = new ResponseDto();
        if (productId == null) {
            return ResponseEntity.badRequest().body(MSG_CODE_1);
        }
        Optional<Product> optProduct = productRepo.findById(productId);
        if (optProduct.isPresent()) {
            response.setMessage(MSG_CODE_7);
            response.setData(convertToDto(optProduct.get()));
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.badRequest().body(MSG_CODE_6);
        }
    }

    public ResponseEntity updateProduct(ProductDto product) {
        ResponseDto response = new ResponseDto();
        if (product.getProductId() == null) {
            return ResponseEntity.badRequest().body(MSG_CODE_1);
        }
        Optional<Product> optProduct = productRepo.findById(product.getProductId());
        if (optProduct.isPresent()) {
            Product data = optProduct.get();
            data.setProductId(product.getProductId());
            data.setProductName(product.getProductName());
            data.setDescription(product.getDescription());
            data.setImageUrl(product.getImageUrl());
            data.setPrice(product.getPrice());
            data.setCategory(categoryRepo.findById(product.getCategoryId()).get());
            data.setStock(product.getStock() != null ? product.getStock() : 0);
            data.setConditionType(product.getConditionType() != null ? product.getConditionType() : "Brand New");
            
            // Handle Auction listings
            if (product.getIsAuction() != null) {
                // If it transitioned to auction
                if (Boolean.TRUE.equals(product.getIsAuction()) && !Boolean.TRUE.equals(data.getIsAuction())) {
                    data.setIsAuction(true);
                    data.setCurrentBid(product.getPrice());
                    data.setBidsCount(0);
                    if (product.getAuctionEndDate() != null) {
                        data.setAuctionEndDate(product.getAuctionEndDate());
                    } else {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_YEAR, 7);
                        data.setAuctionEndDate(cal.getTime());
                    }
                    data.setStock(1);
                } else if (Boolean.TRUE.equals(product.getIsAuction())) {
                    // Update existing auction end date or bid if provided
                    if (product.getAuctionEndDate() != null) {
                        data.setAuctionEndDate(product.getAuctionEndDate());
                    }
                    if (product.getCurrentBid() != null) {
                        data.setCurrentBid(product.getCurrentBid());
                    }
                    if (product.getBidsCount() != null) {
                        data.setBidsCount(product.getBidsCount());
                    }
                    data.setStock(1);
                } else {
                    data.setIsAuction(false);
                    data.setCurrentBid(null);
                    data.setBidsCount(null);
                    data.setAuctionEndDate(null);
                }
            }

            if (product.getStoreId() != null) {
                Optional<Store> store = storeRepo.findById(product.getStoreId());
                store.ifPresent(data::setStore);
            }
            Product updatedProduct = productRepo.save(data);

            response.setMessage(MSG_CODE_8);
            response.setData(convertToDto(updatedProduct));
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.badRequest().body(MSG_CODE_6);
        }
    }

    public ResponseEntity deleteProduct(Integer productId) {
        if (productId == null) {
            return ResponseEntity.badRequest().body(MSG_CODE_1);
        }
        Optional<Product> optProduct = productRepo.findById(productId);
        if (optProduct.isPresent()) {
            productRepo.deleteById(productId);
            Optional<Product> checkProduct = productRepo.findById(productId);
            if (checkProduct.isPresent()) {
                return ResponseEntity.badRequest().body(MSG_CODE_9);
            } else {
                return ResponseEntity.ok().body(MSG_CODE_10);
            }
        } else {
            return ResponseEntity.badRequest().body(MSG_CODE_6);
        }
    }

    public ProductDto convertToDto(Product product) {
        if (product == null) return null;
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
        dto.setIsAuction(product.getIsAuction());
        dto.setCurrentBid(product.getCurrentBid());
        dto.setBidsCount(product.getBidsCount());
        dto.setAuctionEndDate(product.getAuctionEndDate());
        dto.setConditionType(product.getConditionType());
        return dto;
    }

    @Transactional
    public ResponseEntity placeBid(Integer productId, String token, java.math.BigDecimal bidAmount) {
        ResponseDto response = new ResponseDto();
        JwtDataDto jwtData = securityConfig.getJWTData(token);
        if (jwtData.getUserId() == null) {
            response.setMessage("User is not authenticated");
            return ResponseEntity.badRequest().body(response);
        }
        Optional<Product> optProduct = productRepo.findById(productId);
        if (!optProduct.isPresent()) {
            response.setMessage("Product not found");
            return ResponseEntity.badRequest().body(response);
        }
        Product product = optProduct.get();
        if (product.getIsAuction() == null || !product.getIsAuction()) {
            response.setMessage("This item is not listed as an auction");
            return ResponseEntity.badRequest().body(response);
        }
        if (product.getAuctionEndDate() != null && product.getAuctionEndDate().before(new Date())) {
            response.setMessage("This auction has already ended");
            return ResponseEntity.badRequest().body(response);
        }
        java.math.BigDecimal currentMin = product.getCurrentBid() != null ? product.getCurrentBid() : product.getPrice();
        if (currentMin == null) {
            currentMin = java.math.BigDecimal.ZERO;
        }
        if (bidAmount.compareTo(currentMin) <= 0) {
            response.setMessage("Bid must be greater than the current bid of LKR " + currentMin);
            return ResponseEntity.badRequest().body(response);
        }

        product.setCurrentBid(bidAmount);
        product.setBidsCount((product.getBidsCount() != null ? product.getBidsCount() : 0) + 1);
        Product updatedProduct = productRepo.save(product);

        response.setMessage("Bid placed successfully!");
        response.setData(convertToDto(updatedProduct));
        return ResponseEntity.ok().body(response);
    }
}

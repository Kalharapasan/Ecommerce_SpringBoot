package com.example.backend.service;
import com.example.backend.dto.ProductDto;
import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.model.Category;
import com.example.backend.model.Product;
import com.example.backend.repository.CategoryRepo;
import com.example.backend.repository.ProductRepo;
import com.example.backend.repository.StoreRepo;
import com.example.backend.model.Store;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public ProductService(ProductRepo productRepo, CategoryRepo categoryRepo, StoreRepo storeRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.storeRepo = storeRepo;
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
            Product data = new Product();
            data.setProductId(product.getProductId());
            data.setProductName(product.getProductName());
            data.setDescription(product.getDescription());
            data.setImageUrl(product.getImageUrl());
            data.setPrice(product.getPrice());
            data.setCategory(categoryRepo.findById(product.getCategoryId()).get());
            data.setStock(product.getStock() != null ? product.getStock() : 0);
            if (product.getStoreId() != null) {
                Optional<Store> store = storeRepo.findById(product.getStoreId());
                store.ifPresent(data::setStore);
            } else {
                data.setStore(optProduct.get().getStore());
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
        return dto;
    }
}

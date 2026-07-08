package com.example.backend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDto {

    private Integer productId;
    private @NotNull String productName;
    private @NotNull String description;
    private @NotNull String imageUrl;
    private @NotNull BigDecimal price;
    private Integer categoryId;
    private String categoryName;
    private Integer stock;
    private Integer storeId;
    private String storeName;
    private String storeLogoUrl;
    private Boolean storeVerified;
    private BigDecimal storeRating;
    private Integer storeReviewsCount;
}
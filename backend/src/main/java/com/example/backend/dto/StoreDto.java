package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoreDto {
    private Integer storeId;
    private Integer ownerId;
    private String ownerName;
    private String storeName;
    private String storeDescription;
    private String storeLogoUrl;
    private String storeBannerUrl;
    private String storePolicies;
    private Boolean isVerified;
    private Integer followersCount;
    private BigDecimal rating;
    private Integer reviewsCount;
    private Date createdDate;
}

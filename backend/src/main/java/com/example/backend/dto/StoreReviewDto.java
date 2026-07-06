package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StoreReviewDto {
    private Integer reviewId;
    private Integer storeId;
    private Integer userId;
    private String username;
    private Integer rating;
    private String comment;
    private Date createdDate;
}

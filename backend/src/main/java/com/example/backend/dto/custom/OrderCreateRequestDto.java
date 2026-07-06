package com.example.backend.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderCreateRequestDto {
    private String token;
    private String address;
    private String paymentMethod;
    private BigDecimal discountPercent;
}

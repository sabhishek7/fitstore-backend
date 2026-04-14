package com.fitstore.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDto {

    private Long id;
    private String orderNumber;
    private String name;
    private String phone;
    private String street;
    private String landmark;
    private String city;
    private String state;
    private String pincode;
    private String product;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime statusUpdatedAt;
}

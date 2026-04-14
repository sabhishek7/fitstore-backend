package com.fitstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderStatusUpdateDto {

    @NotBlank(message = "Status is required")
    private String status; // Must match OrderStatus enum value
}

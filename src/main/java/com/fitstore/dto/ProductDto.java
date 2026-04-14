package com.fitstore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {

    private Long id; // null for create, present for update responses

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String description;

    private List<String> benefits;

    private String category;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock = 0;

    private Boolean isActive = true;
}

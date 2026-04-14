package com.fitstore.service;

import com.fitstore.dto.ProductDto;
import com.fitstore.entity.Product;
import com.fitstore.exception.ResourceNotFoundException;
import com.fitstore.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // Public: get only active products
    public List<ProductDto> getActiveProducts() {
        return productRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Admin: get all products (including inactive)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Get single product by ID
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toDto(product);
    }

    // Create a new product
    public ProductDto createProduct(ProductDto dto) {
        Product product = toEntity(dto);
        Product saved = productRepository.save(product);
        log.info("Product created: {} (ID: {})", saved.getTitle(), saved.getId());
        return toDto(saved);
    }

    // Update an existing product
    public ProductDto updateProduct(Long id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setTitle(dto.getTitle());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setDescription(dto.getDescription());
        product.setBenefits(serializeBenefits(dto.getBenefits()));
        product.setCategory(dto.getCategory());
        product.setStock(dto.getStock());
        product.setIsActive(dto.getIsActive());

        Product saved = productRepository.save(product);
        log.info("Product updated: {} (ID: {})", saved.getTitle(), saved.getId());
        return toDto(saved);
    }

    // Soft-delete a product
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setIsActive(false);
        productRepository.save(product);
        log.info("Product deactivated: {} (ID: {})", product.getTitle(), product.getId());
    }

    // Count active products (for dashboard)
    public long countActiveProducts() {
        return productRepository.countByIsActiveTrue();
    }

    // --- Mapping helpers ---

    private ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setDescription(product.getDescription());
        dto.setBenefits(deserializeBenefits(product.getBenefits()));
        dto.setCategory(product.getCategory());
        dto.setStock(product.getStock());
        dto.setIsActive(product.getIsActive());
        return dto;
    }

    private Product toEntity(ProductDto dto) {
        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setPrice(dto.getPrice());
        product.setImageUrl(dto.getImageUrl());
        product.setDescription(dto.getDescription());
        product.setBenefits(serializeBenefits(dto.getBenefits()));
        product.setCategory(dto.getCategory());
        product.setStock(dto.getStock() != null ? dto.getStock() : 0);
        product.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return product;
    }

    private String serializeBenefits(List<String> benefits) {
        if (benefits == null || benefits.isEmpty()) return "[]";
        try {
            return objectMapper.writeValueAsString(benefits);
        } catch (Exception e) {
            log.warn("Failed to serialize benefits: {}", e.getMessage());
            return "[]";
        }
    }

    private List<String> deserializeBenefits(String benefits) {
        if (benefits == null || benefits.isBlank()) return List.of();
        try {
            return objectMapper.readValue(benefits, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Failed to deserialize benefits: {}", e.getMessage());
            return List.of();
        }
    }
}

package com.fitstore.controller;

import com.fitstore.dto.*;
import com.fitstore.service.AdminAuthService;
import com.fitstore.service.OrderService;
import com.fitstore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin panel endpoints — protected by AdminAuthFilter.
 * All endpoints under /api/admin/* require a valid Bearer token (except /login).
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // ==================== Authentication ====================

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDto> login(@Valid @RequestBody AdminLoginDto loginDto) {
        AdminLoginResponseDto response = adminAuthService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    // ==================== Product Management ====================

    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Product deactivated"));
    }

    // ==================== Order Management ====================

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders(
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(orderService.getAllOrders(status));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateDto statusDto) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, statusDto.getStatus()));
    }

    // ==================== Dashboard ====================

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboard() {
        DashboardStatsDto stats = orderService.getDashboardStats();
        stats.setActiveProducts(productService.countActiveProducts());
        return ResponseEntity.ok(stats);
    }
}

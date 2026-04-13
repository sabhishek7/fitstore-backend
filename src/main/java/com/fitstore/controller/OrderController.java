package com.fitstore.controller;

import com.fitstore.dto.OrderDto;
import com.fitstore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<Map<String, String>> placeOrder(@Valid @RequestBody OrderDto orderDto) {
        orderService.saveOrder(orderDto);
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Order placed successfully");
        
        return ResponseEntity.ok(response);
    }
}

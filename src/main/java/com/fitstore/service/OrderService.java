package com.fitstore.service;

import com.fitstore.dto.DashboardStatsDto;
import com.fitstore.dto.OrderDto;
import com.fitstore.dto.OrderResponseDto;
import com.fitstore.entity.Order;
import com.fitstore.enums.OrderStatus;
import com.fitstore.exception.ResourceNotFoundException;
import com.fitstore.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Save a new order placed from the storefront.
     */
    public Order saveOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setName(orderDto.getName());
        order.setPhone(orderDto.getPhone());
        order.setStreet(orderDto.getStreet());
        order.setLandmark(orderDto.getLandmark());
        order.setCity(orderDto.getCity());
        order.setState(orderDto.getState());
        order.setPincode(orderDto.getPincode());
        order.setProduct(orderDto.getProduct());
        order.setStatus(OrderStatus.PLACED);
        order.setQuantity(1);

        Order savedOrder = orderRepository.save(order);

        // Generate order number after save (so we have the ID)
        savedOrder.setOrderNumber("ORD-" + String.format("%04d", savedOrder.getId()));
        savedOrder = orderRepository.save(savedOrder);

        log.info("New order placed: {}, Product: {}, User: {}",
                savedOrder.getOrderNumber(), savedOrder.getProduct(), savedOrder.getName());
        return savedOrder;
    }

    /**
     * Admin: Get all orders, optionally filtered by status.
     */
    public List<OrderResponseDto> getAllOrders(String status) {
        List<Order> orders;

        if (status != null && !status.isBlank()) {
            try {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderRepository.findByStatusOrderByCreatedAtDesc(orderStatus);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid order status filter: {}", status);
                orders = orderRepository.findAllByOrderByCreatedAtDesc();
            }
        } else {
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        }

        return orders.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Admin: Update order status.
     */
    public OrderResponseDto updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            order.setStatusUpdatedAt(LocalDateTime.now());
            Order saved = orderRepository.save(order);

            log.info("Order {} status updated to {}", saved.getOrderNumber(), newStatus);
            return toResponseDto(saved);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
    }

    /**
     * Admin: Get dashboard statistics.
     */
    public DashboardStatsDto getDashboardStats() {
        long totalOrders = orderRepository.count();

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayOrders = orderRepository.countByCreatedAtAfter(todayStart);

        long pendingOrders = orderRepository.countByStatus(OrderStatus.PLACED)
                + orderRepository.countByStatus(OrderStatus.CONFIRMED);

        // Calculate total revenue from delivered orders
        List<Order> allOrders = orderRepository.findAll();
        BigDecimal totalRevenue = allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .map(o -> o.getTotalPrice() != null ? o.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalOrders(totalOrders);
        stats.setTodayOrders(todayOrders);
        stats.setTotalRevenue(totalRevenue);
        stats.setPendingOrders(pendingOrders);
        // activeProducts is set by the controller

        return stats;
    }

    // --- Mapping helper ---

    private OrderResponseDto toResponseDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setName(order.getName());
        dto.setPhone(order.getPhone());
        dto.setStreet(order.getStreet());
        dto.setLandmark(order.getLandmark());
        dto.setCity(order.getCity());
        dto.setState(order.getState());
        dto.setPincode(order.getPincode());
        dto.setProduct(order.getProduct());
        dto.setProductId(order.getProductId());
        dto.setQuantity(order.getQuantity());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : "PLACED");
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatusUpdatedAt(order.getStatusUpdatedAt());
        return dto;
    }
}

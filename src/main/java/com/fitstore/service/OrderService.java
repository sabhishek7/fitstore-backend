package com.fitstore.service;

import com.fitstore.dto.OrderDto;
import com.fitstore.entity.Order;
import com.fitstore.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

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

        Order savedOrder = orderRepository.save(order);
        log.info("New order placed: ID: {}, Product: {}, User: {}", savedOrder.getId(), savedOrder.getProduct(), savedOrder.getName());
        return savedOrder;
    }
}

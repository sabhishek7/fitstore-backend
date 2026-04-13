package com.fitstore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private String phone;
    
    private String street;
    
    private String landmark;
    
    private String city;
    
    private String state;
    
    private String pincode;

    private String product;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}

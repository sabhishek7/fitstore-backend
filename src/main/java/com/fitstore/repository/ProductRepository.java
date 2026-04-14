package com.fitstore.repository;

import com.fitstore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsActiveTrueOrderByCreatedAtDesc();

    List<Product> findAllByOrderByCreatedAtDesc();

    long countByIsActiveTrue();
}

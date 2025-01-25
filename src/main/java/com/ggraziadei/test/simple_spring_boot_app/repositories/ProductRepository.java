package com.ggraziadei.test.simple_spring_boot_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ggraziadei.test.simple_spring_boot_app.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

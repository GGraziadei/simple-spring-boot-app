package com.ggraziadei.test.simple_spring_boot_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ggraziadei.test.simple_spring_boot_app.entities.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}

package com.ggraziadei.test.simple_spring_boot_app.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "PRICES", 
indexes = {
    @Index(name = "idx_date_interval", columnList = "START_DATE, END_DATE"),
    @Index(name = "idx_priority", columnList = "PRIORITY"),
    @Index(name = "idx_product_id", columnList = "PRODUCT_ID"),
    @Index(name = "idx_brand_id", columnList = "BRAND_ID"),
})
@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Price {

    @Id
    @UuidGenerator
    @Column(name = "PRICE_ID", nullable = false, length = 36)
    private String id;

    @Column(name = "START_DATE", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "END_DATE", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "PRIORITY", nullable = false)
    private int priority;

    @Column(name = "PRICE_LIST", nullable = false)
    private int priceList;

    @Column(name = "PRICE", nullable = false)
    private double price;

    @Column(name = "CURRENCY", nullable = false, length = 3)
    private String currency;

    // relationship with Product
    @ManyToOne(targetEntity = Product.class)
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    private Product product;

    // relationship with Brand
    @ManyToOne(targetEntity = Brand.class)
    @JoinColumn(name = "BRAND_ID", nullable = false)
    private Brand brand;

}

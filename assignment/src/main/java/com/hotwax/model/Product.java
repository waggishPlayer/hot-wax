package com.hotwax.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "product")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;
    
    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;
    
    @Column(length = 30)
    private String color;
    
    @Column(length = 10)
    private String size;
}

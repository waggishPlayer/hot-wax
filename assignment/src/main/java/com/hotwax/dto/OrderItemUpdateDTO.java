package com.hotwax.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemUpdateDTO {
    
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    private String status;
}

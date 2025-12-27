package com.hotwax.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderUpdateDTO {
    
    private Integer shippingContactMechId;
    
    private Integer billingContactMechId;
}

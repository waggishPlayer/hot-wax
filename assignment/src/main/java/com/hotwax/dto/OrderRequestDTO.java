package com.hotwax.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderRequestDTO {
    
    @NotNull(message = "Order date is required")
    private LocalDate orderDate;
    
    @NotNull(message = "Customer ID is required")
    private Integer customerId;
    
    @NotNull(message = "Shipping contact mechanism ID is required")
    private Integer shippingContactMechId;
    
    @NotNull(message = "Billing contact mechanism ID is required")
    private Integer billingContactMechId;
    
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemDTO> orderItems;
}

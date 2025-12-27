package com.hotwax.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Integer orderId;
    private LocalDate orderDate;
    private Integer customerId;
    private String customerName;
    private Integer shippingContactMechId;
    private Integer billingContactMechId;
    private List<OrderItemResponseDTO> orderItems;
}

package com.hotwax.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Integer orderItemSeqId;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private String status;
}

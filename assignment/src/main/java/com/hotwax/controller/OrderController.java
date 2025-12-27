package com.hotwax.controller;

import com.hotwax.dto.*;
import com.hotwax.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        OrderResponseDTO response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{order_id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable("order_id") Integer orderId) {
        OrderResponseDTO response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{order_id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @PathVariable("order_id") Integer orderId,
            @Valid @RequestBody OrderUpdateDTO updateDTO) {
        OrderResponseDTO response = orderService.updateOrder(orderId, updateDTO);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{order_id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("order_id") Integer orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{order_id}/items")
    public ResponseEntity<OrderItemResponseDTO> addOrderItem(
            @PathVariable("order_id") Integer orderId,
            @Valid @RequestBody OrderItemDTO itemDTO) {
        OrderItemResponseDTO response = orderService.addOrderItem(orderId, itemDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PutMapping("/{order_id}/items/{order_item_seq_id}")
    public ResponseEntity<OrderItemResponseDTO> updateOrderItem(
            @PathVariable("order_id") Integer orderId,
            @PathVariable("order_item_seq_id") Integer orderItemSeqId,
            @Valid @RequestBody OrderItemUpdateDTO updateDTO) {
        OrderItemResponseDTO response = orderService.updateOrderItem(orderId, orderItemSeqId, updateDTO);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{order_id}/items/{order_item_seq_id}")
    public ResponseEntity<Void> deleteOrderItem(
            @PathVariable("order_id") Integer orderId,
            @PathVariable("order_item_seq_id") Integer orderItemSeqId) {
        orderService.deleteOrderItem(orderId, orderItemSeqId);
        return ResponseEntity.noContent().build();
    }
}

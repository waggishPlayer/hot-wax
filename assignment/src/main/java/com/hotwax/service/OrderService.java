package com.hotwax.service;

import com.hotwax.dto.*;
import com.hotwax.model.*;
import com.hotwax.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderHeaderRepository orderHeaderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
        // Validate customer exists
        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Customer not found with ID: " + orderRequest.getCustomerId()));
        
        // Create order header
        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomer(customer);
        orderHeader.setStatus("PENDING");
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // Process each order item
        for (OrderItemDTO itemDTO : orderRequest.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Product not found with ID: " + itemDTO.getProductId()));
            
            // Check stock availability
            if (product.getStockQuantity() < itemDTO.getQuantity()) {
                throw new IllegalStateException(
                        "Insufficient stock for product: " + product.getName() + 
                        ". Available: " + product.getStockQuantity() + 
                        ", Requested: " + itemDTO.getQuantity());
            }
            
            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            orderItem.setSubtotal(subtotal);
            
            // Update stock
            product.setStockQuantity(product.getStockQuantity() - itemDTO.getQuantity());
            productRepository.save(product);
            
            // Add item to order
            orderHeader.addOrderItem(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }
        
        orderHeader.setTotalAmount(totalAmount);
        
        // Save order
        OrderHeader savedOrder = orderHeaderRepository.save(orderHeader);
        
        // Build response DTO
        return buildOrderResponseDTO(savedOrder);
    }
    
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Long orderId) {
        OrderHeader orderHeader = orderHeaderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        
        return buildOrderResponseDTO(orderHeader);
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderHeaderRepository.findAll().stream()
                .map(this::buildOrderResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Customer not found with ID: " + customerId);
        }
        
        return orderHeaderRepository.findByCustomerId(customerId).stream()
                .map(this::buildOrderResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, String status) {
        OrderHeader orderHeader = orderHeaderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        
        orderHeader.setStatus(status.toUpperCase());
        OrderHeader updatedOrder = orderHeaderRepository.save(orderHeader);
        
        return buildOrderResponseDTO(updatedOrder);
    }
    
    @Transactional
    public void cancelOrder(Long orderId) {
        OrderHeader orderHeader = orderHeaderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        
        if (!"PENDING".equals(orderHeader.getStatus())) {
            throw new IllegalStateException("Only PENDING orders can be cancelled");
        }
        
        // Restore stock quantities
        for (OrderItem item : orderHeader.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }
        
        orderHeader.setStatus("CANCELLED");
        orderHeaderRepository.save(orderHeader);
    }
    
    private OrderResponseDTO buildOrderResponseDTO(OrderHeader orderHeader) {
        List<OrderItemResponseDTO> itemResponses = orderHeader.getOrderItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());
        
        String customerName = orderHeader.getCustomer().getFirstName() + " " + 
                             orderHeader.getCustomer().getLastName();
        
        return new OrderResponseDTO(
                orderHeader.getId(),
                orderHeader.getCustomer().getId(),
                customerName,
                orderHeader.getOrderDate(),
                orderHeader.getTotalAmount(),
                orderHeader.getStatus(),
                itemResponses
        );
    }
}

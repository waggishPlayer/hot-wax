package com.hotwax.service;

import com.hotwax.dto.*;
import com.hotwax.model.*;
import com.hotwax.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderHeaderRepository orderHeaderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ContactMechRepository contactMechRepository;
    private final ProductRepository productRepository;
    
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + request.getCustomerId()));
        
        ContactMech shippingContact = contactMechRepository.findById(request.getShippingContactMechId())
                .orElseThrow(() -> new EntityNotFoundException("Shipping contact not found with ID: " + request.getShippingContactMechId()));
        
        ContactMech billingContact = contactMechRepository.findById(request.getBillingContactMechId())
                .orElseThrow(() -> new EntityNotFoundException("Billing contact not found with ID: " + request.getBillingContactMechId()));
        
        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setOrderDate(request.getOrderDate());
        orderHeader.setCustomer(customer);
        orderHeader.setShippingContactMech(shippingContact);
        orderHeader.setBillingContactMech(billingContact);
        
        OrderHeader savedOrder = orderHeaderRepository.save(orderHeader);
        
        for (OrderItemDTO itemDTO : request.getOrderItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + itemDTO.getProductId()));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getOrderId());
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setStatus(itemDTO.getStatus());
            
            orderItemRepository.save(orderItem);
        }
        
        return getOrderById(savedOrder.getOrderId());
    }
    
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(Integer orderId) {
        OrderHeader orderHeader = orderHeaderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        
        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
        
        return buildOrderResponseDTO(orderHeader, items);
    }
    
    @Transactional
    public OrderResponseDTO updateOrder(Integer orderId, OrderUpdateDTO updateDTO) {
        OrderHeader orderHeader = orderHeaderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
        
        if (updateDTO.getShippingContactMechId() != null) {
            ContactMech shippingContact = contactMechRepository.findById(updateDTO.getShippingContactMechId())
                    .orElseThrow(() -> new EntityNotFoundException("Shipping contact not found with ID: " + updateDTO.getShippingContactMechId()));
            orderHeader.setShippingContactMech(shippingContact);
        }
        
        if (updateDTO.getBillingContactMechId() != null) {
            ContactMech billingContact = contactMechRepository.findById(updateDTO.getBillingContactMechId())
                    .orElseThrow(() -> new EntityNotFoundException("Billing contact not found with ID: " + updateDTO.getBillingContactMechId()));
            orderHeader.setBillingContactMech(billingContact);
        }
        
        orderHeaderRepository.save(orderHeader);
        
        return getOrderById(orderId);
    }
    
    @Transactional
    public void deleteOrder(Integer orderId) {
        if (!orderHeaderRepository.existsById(orderId)) {
            throw new EntityNotFoundException("Order not found with ID: " + orderId);
        }
        orderHeaderRepository.deleteById(orderId);
    }
    
    @Transactional
    public OrderItemResponseDTO addOrderItem(Integer orderId, OrderItemDTO itemDTO) {
        if (!orderHeaderRepository.existsById(orderId)) {
            throw new EntityNotFoundException("Order not found with ID: " + orderId);
        }
        
        Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + itemDTO.getProductId()));
        
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setProduct(product);
        orderItem.setQuantity(itemDTO.getQuantity());
        orderItem.setStatus(itemDTO.getStatus());
        
        OrderItem savedItem = orderItemRepository.save(orderItem);
        
        return new OrderItemResponseDTO(
                savedItem.getOrderItemSeqId(),
                product.getProductId(),
                product.getProductName(),
                savedItem.getQuantity(),
                savedItem.getStatus()
        );
    }
    
    @Transactional
    public OrderItemResponseDTO updateOrderItem(Integer orderId, Integer orderItemSeqId, OrderItemUpdateDTO updateDTO) {
        OrderItem orderItem = orderItemRepository.findByOrderItemSeqIdAndOrderId(orderItemSeqId, orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order item not found with ID: " + orderItemSeqId + " for order: " + orderId));
        
        if (updateDTO.getQuantity() != null) {
            orderItem.setQuantity(updateDTO.getQuantity());
        }
        
        if (updateDTO.getStatus() != null) {
            orderItem.setStatus(updateDTO.getStatus());
        }
        
        OrderItem updatedItem = orderItemRepository.save(orderItem);
        
        return new OrderItemResponseDTO(
                updatedItem.getOrderItemSeqId(),
                updatedItem.getProduct().getProductId(),
                updatedItem.getProduct().getProductName(),
                updatedItem.getQuantity(),
                updatedItem.getStatus()
        );
    }
    
    @Transactional
    public void deleteOrderItem(Integer orderId, Integer orderItemSeqId) {
        if (!orderItemRepository.findByOrderItemSeqIdAndOrderId(orderItemSeqId, orderId).isPresent()) {
            throw new EntityNotFoundException("Order item not found with ID: " + orderItemSeqId + " for order: " + orderId);
        }
        orderItemRepository.deleteByOrderItemSeqIdAndOrderId(orderItemSeqId, orderId);
    }
    
    private OrderResponseDTO buildOrderResponseDTO(OrderHeader orderHeader, List<OrderItem> items) {
        List<OrderItemResponseDTO> itemDTOs = items.stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getOrderItemSeqId(),
                        item.getProduct().getProductId(),
                        item.getProduct().getProductName(),
                        item.getQuantity(),
                        item.getStatus()
                ))
                .collect(Collectors.toList());
        
        String customerName = orderHeader.getCustomer().getFirstName() + " " + orderHeader.getCustomer().getLastName();
        
        return new OrderResponseDTO(
                orderHeader.getOrderId(),
                orderHeader.getOrderDate(),
                orderHeader.getCustomer().getCustomerId(),
                customerName,
                orderHeader.getShippingContactMech().getContactMechId(),
                orderHeader.getBillingContactMech().getContactMechId(),
                itemDTOs
        );
    }
}

package com.hotwax.controller;

import com.hotwax.model.ContactMech;
import com.hotwax.model.Customer;
import com.hotwax.model.Product;
import com.hotwax.repository.ContactMechRepository;
import com.hotwax.repository.CustomerRepository;
import com.hotwax.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DataController {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ContactMechRepository contactMechRepository;

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerRepository.findAll());
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/contacts")
    public ResponseEntity<List<ContactMech>> getAllContacts() {
        return ResponseEntity.ok(contactMechRepository.findAll());
    }
}

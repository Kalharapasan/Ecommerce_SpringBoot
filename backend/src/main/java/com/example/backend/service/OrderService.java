package com.example.backend.service;

import com.example.backend.config.SecurityConfig;
import com.example.backend.dto.custom.JwtDataDto;
import com.example.backend.dto.custom.OrderCreateRequestDto;
import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final SecurityConfig securityConfig;

    public OrderService(OrderRepo orderRepo, OrderItemRepo orderItemRepo, CartRepo cartRepo, UserRepo userRepo, ProductRepo productRepo, SecurityConfig securityConfig) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.securityConfig = securityConfig;
    }

    public ResponseEntity createOrder(OrderCreateRequestDto request) {
        ResponseDto response = new ResponseDto();
        if (request.getToken() == null || request.getToken().isEmpty()) {
            return ResponseEntity.badRequest().body("Token is empty");
        }
        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Delivery address is required");
        }
        if (request.getPaymentMethod() == null || request.getPaymentMethod().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Payment method is required");
        }

        JwtDataDto jwtData = securityConfig.getJWTData(request.getToken());
        if (jwtData.getUserId() == null) {
            return ResponseEntity.badRequest().body("User not authenticated");
        }

        Optional<User> optUser = userRepo.findById(jwtData.getUserId());
        if (!optUser.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<Cart> cartItems = cartRepo.findAll().stream()
                .filter(cart -> cart.getUser().getUserId().equals(jwtData.getUserId()))
                .collect(Collectors.toList());

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        // Calculate total price
        BigDecimal total = BigDecimal.ZERO;
        for (Cart cart : cartItems) {
            total = total.add(new BigDecimal(cart.getCount()).multiply(cart.getProduct().getPrice()));
        }

        // Apply discount if any
        BigDecimal discountPercent = request.getDiscountPercent() != null ? request.getDiscountPercent() : BigDecimal.ZERO;
        BigDecimal discountAmount = total.multiply(discountPercent).divide(new BigDecimal(100));
        BigDecimal finalTotal = total.subtract(discountAmount);

        // Create Order
        Order order = new Order();
        order.setUser(optUser.get());
        order.setCreatedDate(new Date());
        order.setTotalPrice(finalTotal.doubleValue());
        order.setAddress(request.getAddress());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus("Pending");

        Order savedOrder = orderRepo.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        // Create Order Items and update product stock
        for (Cart cart : cartItems) {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(cart.getProduct());
            item.setQuantity(cart.getCount());
            item.setPrice(cart.getProduct().getPrice().doubleValue());
            orderItemRepo.save(item);
            orderItems.add(item);

            // Update product stock
            Product product = cart.getProduct();
            if (product.getStock() != null) {
                int newStock = Math.max(0, product.getStock() - cart.getCount());
                product.setStock(newStock);
                productRepo.save(product);
            }

            // Remove from cart
            cartRepo.deleteById(cart.getCartId());
        }

        savedOrder.setOrderItems(orderItems);

        response.setMessage("Order created successfully");
        response.setData(savedOrder);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity getOrdersForUser(String token) {
        ResponseDto response = new ResponseDto();
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("Token is empty");
        }
        JwtDataDto jwtData = securityConfig.getJWTData(token);
        if (jwtData.getUserId() == null) {
            return ResponseEntity.badRequest().body("User not authenticated");
        }

        List<Order> orders = orderRepo.findAll().stream()
                .filter(order -> order.getUser().getUserId().equals(jwtData.getUserId()))
                .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate())) // Newest first
                .collect(Collectors.toList());

        response.setMessage("Orders retrieved successfully");
        response.setData(orders);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity getAllOrders() {
        ResponseDto response = new ResponseDto();
        List<Order> orders = orderRepo.findAll().stream()
                .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate())) // Newest first
                .collect(Collectors.toList());
        response.setMessage("All orders retrieved successfully");
        response.setData(orders);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity updateOrderStatus(Integer orderId, String status) {
        ResponseDto response = new ResponseDto();
        Optional<Order> optOrder = orderRepo.findById(orderId);
        if (optOrder.isPresent()) {
            Order order = optOrder.get();
            order.setStatus(status);
            orderRepo.save(order);
            response.setMessage("Order status updated successfully");
            response.setData(order);
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.badRequest().body("Order not found");
        }
    }
}

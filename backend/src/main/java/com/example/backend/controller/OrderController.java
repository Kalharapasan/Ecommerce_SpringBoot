package com.example.backend.controller;

import com.example.backend.dto.custom.OrderCreateRequestDto;
import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createOrder(@RequestBody OrderCreateRequestDto request) {
        return orderService.createOrder(request);
    }

    @GetMapping("/user/{token}")
    public ResponseEntity<ResponseDto> getOrdersForUser(@PathVariable String token) {
        return orderService.getOrdersForUser(token);
    }
}

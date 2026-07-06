package com.example.backend.controller;

import com.example.backend.dto.CartDto;
import com.example.backend.dto.custom.CalculationDto;
import com.example.backend.dto.custom.ResponseDto;
import com.example.backend.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{token}")
    public ResponseEntity<ResponseDto> addItem(@PathVariable String token, @RequestBody CartDto cartDto) {
        ResponseEntity response = cartService.addItem(token, cartDto);
        return response;
    }

    @GetMapping("/{token}")
    public ResponseEntity<ResponseDto> getItemsForUser(@PathVariable String token) {
        ResponseEntity response = cartService.getItemsForUser(token);
        return response;
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<ResponseDto> updateItem(@PathVariable Integer cartId, @RequestBody CartDto cartDto) {
        ResponseEntity response = cartService.updateItem(cartId, cartDto);
        return response;
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<String> deleteItem(@PathVariable Integer cartId) {
        ResponseEntity response = cartService.deleteItem(cartId);
        return response;
    }

    @GetMapping("/totalCalculation/{userId}")
    public ResponseEntity<CalculationDto> getCalculations(
            @PathVariable Integer userId,
            @RequestParam(required = false, defaultValue = "0") BigDecimal discount
    ) {
        ResponseEntity response = cartService.getCalculations(userId,discount);
        return response;
    }

    @DeleteMapping("/clear/{token}")
    public ResponseEntity<String> clearCart(@PathVariable String token) {
        ResponseEntity response = cartService.clearCart(token);
        return response;
    }
}

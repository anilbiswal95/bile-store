package com.bike.store.cart.controller;

import com.bike.store.cart.dto.AddToCartRequest;
import com.bike.store.cart.dto.CartDto;
import com.bike.store.cart.service.CartService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(cartService.getCart(user.getUsername()));
    }

    @PostMapping("/add")
    public ResponseEntity<CartDto> addToCart(@AuthenticationPrincipal UserDetails user,
                                             @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(user.getUsername(), request));
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<CartDto> updateQuantity(@AuthenticationPrincipal UserDetails user,
                                                  @PathVariable Long productId,
                                                  @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(user.getUsername(), productId, quantity));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartDto> removeItem(@AuthenticationPrincipal UserDetails user,
                                              @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(user.getUsername(), productId));
    }
}

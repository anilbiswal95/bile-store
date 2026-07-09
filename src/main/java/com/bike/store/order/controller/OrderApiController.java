package com.bike.store.order.controller;

import com.bike.store.order.dto.OrderDto;
import com.bike.store.order.dto.PlaceOrderRequest;
import com.bike.store.order.dto.SendConfirmationRequest;
import com.bike.store.order.service.OrderService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(@AuthenticationPrincipal UserDetails user,
                                               @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(user.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getUserOrders(@AuthenticationPrincipal UserDetails user,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(orderService.getUserOrders(user.getUsername(), page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    // ===== CHANGED: Added endpoint to send confirmation email =====
    @PostMapping("/send-confirmation")
    public ResponseEntity<?> sendConfirmationEmail(@Valid @RequestBody SendConfirmationRequest request) {
        orderService.sendOrderConfirmationEmailById(request.getOrderId(), request.getEmail());
        return ResponseEntity.ok().build();
    }
}

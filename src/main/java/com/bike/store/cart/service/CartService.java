package com.bike.store.cart.service;

import com.bike.store.cart.dto.AddToCartRequest;
import com.bike.store.cart.dto.CartDto;
import com.bike.store.cart.entity.Cart;
import com.bike.store.cart.entity.CartItem;
import com.bike.store.cart.repository.CartRepository;
import com.bike.store.common.exception.AppException;
import com.bike.store.common.exception.ResourceNotFoundException;
import com.bike.store.product.entity.Product;
import com.bike.store.product.repository.ProductRepository;
import com.bike.store.user.entity.User;
import com.bike.store.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartDto getCart(String email) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUserIdWithItems(user.getId()).orElse(null);
        return toCartDto(cart);
    }

    @Transactional
    public CartDto addToCart(String email, AddToCartRequest request) {
        User user = getUser(email);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStock() < request.getQuantity()) {
            throw new AppException("Insufficient stock", HttpStatus.BAD_REQUEST);
        }

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart c = Cart.builder()
                            .user(user)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(c);
                });

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + request.getQuantity());
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(item);
        }

        cartRepository.save(cart);
        return getCart(email);
    }

    @Transactional
    public CartDto updateQuantity(String email, Long productId, int quantity) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));

        cartRepository.save(cart);
        return getCart(email);
    }

    @Transactional
    public CartDto removeItem(String email, Long productId) {
        User user = getUser(email);
        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
        return getCart(email);
    }

    @Transactional
    public void clearCart(String email) {
        User user = getUser(email);
        cartRepository.findByUserId(user.getId()).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CartDto toCartDto(Cart cart) {
        CartDto dto = new CartDto();
        if (cart == null || cart.getItems().isEmpty()) {
            dto.setItems(new ArrayList<>());
            dto.setTotal(BigDecimal.ZERO);
            return dto;
        }

        dto.setItems(cart.getItems().stream().map(item -> {
            CartDto.CartItemDto itemDto = new CartDto.CartItemDto();
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setPrice(item.getProduct().getPrice());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setSubtotal(item.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
            itemDto.setImageUrl(item.getProduct().getImageUrl());
            return itemDto;
        }).collect(Collectors.toList()));

        dto.setTotal(dto.getItems().stream()
                .map(CartDto.CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return dto;
    }
}

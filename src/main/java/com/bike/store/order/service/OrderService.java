package com.bike.store.order.service;

import com.bike.store.admin.dto.OrderResponseDto;
import com.bike.store.cart.entity.Cart;
import com.bike.store.cart.entity.CartItem;
import com.bike.store.cart.repository.CartRepository;
import com.bike.store.cart.service.CartService;
import com.bike.store.common.email.EmailService;
import com.bike.store.common.exception.AppException;
import com.bike.store.common.exception.ResourceNotFoundException;
import com.bike.store.order.dto.OrderDto;
import com.bike.store.order.dto.PlaceOrderRequest;
import com.bike.store.order.entity.Order;
import com.bike.store.order.entity.OrderItem;
import com.bike.store.order.entity.OrderStatus;
import com.bike.store.order.repository.OrderRepository;
import com.bike.store.product.entity.Product;
import com.bike.store.product.repository.ProductRepository;
import com.bike.store.user.entity.User;
import com.bike.store.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;

    @Transactional
    public OrderDto placeOrder(String email, PlaceOrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserIdWithItems(user.getId())
                .orElseThrow(() -> new AppException("Cart is empty", HttpStatus.BAD_REQUEST));

        if (cart.getItems().isEmpty()) {
            throw new AppException("Cart is empty", HttpStatus.BAD_REQUEST);
        }

        Order order = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .paymentMethod(request.getPaymentMethod())
                .items(new ArrayList<>())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new AppException("Insufficient stock for: " + product.getName(), HttpStatus.BAD_REQUEST);
            }

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.getItems().add(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalAmount(total);
        orderRepository.save(order);
        cartService.clearCart(email);

        // Send order confirmation email
        sendOrderConfirmationEmail(order, user);

        return toDto(order);
    }

    public List<OrderDto> getUserOrders(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Order> orders = orderRepository.findByUserIdWithItems(user.getId(), PageRequest.of(page, size));
        return orders.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public OrderDto getOrder(Long id) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return toDto(order);
    }

    @Transactional
    public OrderDto updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        return toDto(orderRepository.save(order));
    }

    public Page<Order> getAllOrders(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * Send order confirmation email to user
     */
    private void sendOrderConfirmationEmail(Order order, User user) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", user.getFullName());
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("orderDate", order.getCreatedAt()); // LocalDateTime is fine with #temporals
            variables.put("shippingAddress", order.getShippingAddress());
            variables.put("paymentMethod", order.getPaymentMethod());
            variables.put("totalAmount", order.getTotalAmount());

            // Convert items to simple DTOs or maps
            List<Map<String, Object>> items = order.getItems().stream().map(it -> {
                Map<String, Object> m = new HashMap<>();
                m.put("productName", it.getProductName());
                m.put("quantity", it.getQuantity());
                m.put("price", it.getPrice());
                m.put("subtotal", it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())));
                return m;
            }).collect(Collectors.toList());
            variables.put("items", items);

            emailService.sendHtmlEmail(
                    user.getEmail(),
                    "Order Confirmation - " + order.getOrderNumber(),
                    "order-confirmation",
                    variables
            );
        } catch (Exception e) {
            System.err.println("Failed to send order confirmation email: " + e.getMessage());
        }
    }


    private OrderDto toDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(item -> {
                OrderDto.OrderItemDto itemDto = new OrderDto.OrderItemDto();
                itemDto.setProductName(item.getProductName());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                itemDto.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                return itemDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Return an order for a given id only if it belongs to the user with the given email.
     * Throws ResourceNotFoundException if order not found or does not belong to the user.
     */
    public OrderDto getOrderForUser(Long id, String userEmail) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Ensure the order belongs to the requesting user
        if (order.getUser() == null || !order.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new ResourceNotFoundException("Order not found");
        }

        return toDto(order);
    }
    /*public List<OrderResponseDto> getAllOrdersForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findAll(PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(order -> {
                    OrderResponseDto dto = new OrderResponseDto();
                    dto.setId(order.getId());
                    dto.setOrderNumber(order.getOrderNumber());
                    dto.setUserEmail(order.getUser().getEmail());
                    dto.setStatus(order.getStatus().name());
                    dto.setTotalAmount(order.getTotalAmount());
                    dto.setCreatedAt(order.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }*/
    /**
     * CHANGED: Updated to use OrderResponseDto with eager loading
     * Uses JOIN FETCH to load User data in the same query
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrdersForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // CHANGED: Use custom query with JOIN FETCH to load user data eagerly
        Page<Order> ordersPage = orderRepository.findAllWithUserAndItems(pageable);

        return ordersPage.getContent().stream()
                .map(this::convertToAdminOrderDto)
                .collect(Collectors.toList());
    }

    /**
     * CHANGED: New method to convert Order to OrderResponseDto
     * Handles the User data safely since it's loaded eagerly
     */
    private OrderResponseDto convertToAdminOrderDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        // CHANGED: Safe access to user since it's loaded via JOIN FETCH
        User user = order.getUser();
        if (user != null) {
            dto.setUserEmail(user.getEmail());
            dto.setUserName(user.getFullName());
        } else {
            dto.setUserEmail("N/A");
            dto.setUserName("N/A");
        }

        return dto;
    }

}

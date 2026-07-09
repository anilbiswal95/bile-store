package com.bike.store.product.controller;

import com.bike.store.cart.dto.CartDto;
import com.bike.store.cart.service.CartService;
import com.bike.store.common.dto.PagedResponse;
import com.bike.store.common.exception.ResourceNotFoundException;
import com.bike.store.order.dto.OrderDto;
import com.bike.store.product.dto.ProductDto;
import com.bike.store.product.service.ProductService;
import com.bike.store.order.service.OrderService;
import com.bike.store.user.entity.User;
import com.bike.store.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final CartService cartService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featured", productService.getFeaturedProducts());
        model.addAttribute("categories", productService.getAllCategories());
        return "home";
    }

    /*@GetMapping("/products")
    public String products(@RequestParam(required = false) Long categoryId,
                           @RequestParam(required = false) BigDecimal minPrice,
                           @RequestParam(required = false) BigDecimal maxPrice,
                           @RequestParam(required = false) String bikeModel,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        PagedResponse<ProductDto> products = productService.getProducts(categoryId, minPrice, maxPrice, bikeModel, page, 12);
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("bikeModels", productService.getBikeModels());
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("selectedBikeModel", bikeModel);
        return "products";
    }*/

    @GetMapping("/products")
    public String products(@RequestParam(required = false) Long categoryId,
                           @RequestParam(required = false) String minPrice,
                           @RequestParam(required = false) String maxPrice,
                           @RequestParam(required = false) String bikeModel,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {

        // Convert empty strings to null so repository sees "no filter"
        BigDecimal min = (minPrice == null || minPrice.isBlank()) ? null : new BigDecimal(minPrice);
        BigDecimal max = (maxPrice == null || maxPrice.isBlank()) ? null : new BigDecimal(maxPrice);
        String modelFilter = (bikeModel == null || bikeModel.isBlank()) ? null : bikeModel;

        // Optional: log the parsed filters for debugging
        // log.debug("Products filter: categoryId={}, min={}, max={}, bikeModel={}, page={}",
        //           categoryId, min, max, modelFilter, page);

        PagedResponse<ProductDto> products = productService.getProducts(categoryId, min, max, modelFilter, page, 12);
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("bikeModels", productService.getBikeModels());
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("selectedBikeModel", modelFilter);
        model.addAttribute("minPrice", min);
        model.addAttribute("maxPrice", max);
        return "products";
    }


    /*@GetMapping("/products/{id}")
    public String productDetail(@PathVariable long id, Model model) {
        model.addAttribute("product", productService.getProduct(id));
        return "product-detail";
    }*/

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable long id, Model model) {
        try {
            ProductDto product = productService.getProduct(id);
            model.addAttribute("product", product);
            return "product-detail";
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("message", "Product not found");
            return "not-found"; // create templates/not-found.html
        }
    }

    @GetMapping("/search")
    public String search(@RequestParam String q,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {
        model.addAttribute("products", productService.searchProducts(q, page, 12));
        model.addAttribute("query", q);
        return "search";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /*@GetMapping("/cart")
    public String cartPage() {
        return "cart";
    }*/

    @GetMapping("/cart")
    public String cartPage(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("message", "Please log in to view your cart");
            return "login"; // or redirect to login
        }

        User user = userService.getByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CartDto cart = cartService.getCartForUser(user.getId());
        if (cart == null) {
            cart = new CartDto(); // empty cart
            cart.setItems(Collections.emptyList());
            cart.setTotal(BigDecimal.ZERO);
        }

        model.addAttribute("cart", cart);
        return "cart";
    }





    @GetMapping("/orders")
    public String ordersPage(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model,
                             Principal principal) {
        List<OrderDto> orders = orderService.getUserOrders(principal.getName(), page, size);
        model.addAttribute("orders", orders);
        return "orders";
    }

    /*@GetMapping("/orders")
    public String ordersPage() {
        return "orders";
    }*/

    @GetMapping("/admin")
    public String adminPage() {
        return "admin/dashboard";
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable("id") Long id, Model model, Principal principal) {
        if (principal == null) {
            // Not logged in — redirect to login or show message
            model.addAttribute("message", "Please log in to view your order");
            return "login";
        }

        try {
            OrderDto order = orderService.getOrderForUser(id, principal.getName());
            model.addAttribute("order", order);
            return "order-detail"; // create templates/order-detail.html or reuse an existing view
        } catch (ResourceNotFoundException ex) {
            model.addAttribute("message", "Order not found");
            return "not-found";
        }
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        return "checkout";
    }
}

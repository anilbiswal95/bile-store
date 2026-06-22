package com.bike.store.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Admin UI Controller for rendering admin pages.
 * All endpoints are secured and require ROLE_ADMIN.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products() {
        return "admin/dashboard";  // Using the same dashboard for now
    }

    @GetMapping("/categories")
    public String categories() {
        return "admin/dashboard";  // Using the same dashboard for now
    }

    @GetMapping("/orders")
    public String orders() {
        return "admin/dashboard";  // Using the same dashboard for now
    }
}


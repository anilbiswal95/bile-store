package com.bike.store.user.controller;

import com.bike.store.user.dto.AddressDto;
import com.bike.store.user.entity.User;
import com.bike.store.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * User Controller for handling user profile and related views.
 */
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Display user profile page with user details and saved addresses.
     *
     * @param userDetails Currently authenticated user details
     * @param model Spring MVC model to pass data to view
     * @return profile.html view
     */
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Get current user from database
        User user = userService.getCurrentUser(userDetails.getUsername());
        model.addAttribute("user", user);

        // Get all saved addresses for the current user (max 4 addresses allowed)
        List<AddressDto> addresses = userService.getAddresses(userDetails.getUsername());
        model.addAttribute("addresses", addresses);

        return "profile"; // renders profile.html
    }
}
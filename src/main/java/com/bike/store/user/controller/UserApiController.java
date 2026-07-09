package com.bike.store.user.controller;

import com.bike.store.user.dto.AddressDto;
import com.bike.store.user.entity.User;
import com.bike.store.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    // ===== GET USER PROFILE =====
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal UserDetails user) {
        User userEntity = userService.getCurrentUser(user.getUsername());
        return ResponseEntity.ok(userEntity);
    }

    // ===== UPDATE USER PROFILE =====
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal UserDetails user,
                                              @RequestBody Map<String, String> updates) {
        User userEntity = userService.getCurrentUser(user.getUsername());

        if (updates.containsKey("email") && updates.get("email") != null && !updates.get("email").isEmpty()) {
            userEntity.setEmail(updates.get("email"));
        }

        if (updates.containsKey("mobile") && updates.get("mobile") != null && !updates.get("mobile").isEmpty()) {
            userEntity.setMobile(updates.get("mobile"));
        }

        User updated = userService.updateUser(userEntity);
        return ResponseEntity.ok(updated);
    }

    // ===== GET ADDRESSES =====
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getAddresses(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(userService.getAddresses(user.getUsername()));
    }

    // ===== ADD NEW ADDRESS =====
    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> addAddress(@AuthenticationPrincipal UserDetails user,
                                                 @Valid @RequestBody AddressDto dto) {
        return ResponseEntity.ok(userService.addAddress(user.getUsername(), dto));
    }

    // ===== UPDATE ADDRESS =====
    // CHANGED: Added endpoint to update existing address
    @PutMapping("/addresses/{id}")
    public ResponseEntity<AddressDto> updateAddress(@AuthenticationPrincipal UserDetails user,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody AddressDto dto) {
        return ResponseEntity.ok(userService.updateAddress(user.getUsername(), id, dto));
    }

    // ===== DELETE ADDRESS =====
    // CHANGED: Added endpoint to delete address
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@AuthenticationPrincipal UserDetails user,
                                              @PathVariable Long id) {
        userService.deleteAddress(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
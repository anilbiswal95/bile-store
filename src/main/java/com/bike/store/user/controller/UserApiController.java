package com.bike.store.user.controller;

import com.bike.store.user.dto.AddressDto;
import com.bike.store.user.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDto>> getAddresses(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(userService.getAddresses(user.getUsername()));
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDto> addAddress(@AuthenticationPrincipal UserDetails user,
                                                 @Valid @RequestBody AddressDto dto) {
        return ResponseEntity.ok(userService.addAddress(user.getUsername(), dto));
    }
}

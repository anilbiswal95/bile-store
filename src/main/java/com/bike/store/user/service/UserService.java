package com.bike.store.user.service;

import com.bike.store.common.exception.AppException;
import com.bike.store.user.dto.*;
import com.bike.store.user.entity.Address;
import com.bike.store.user.entity.Role;
import com.bike.store.user.entity.User;
import com.bike.store.user.repository.AddressRepository;
import com.bike.store.user.repository.UserRepository;
import com.bike.store.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already registered", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .mobile(request.getMobile())
                .enabled(true)
                .roles(Collections.singleton(Role.ROLE_USER))
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);
        String role = user.getRoles().iterator().next().name();

        return new AuthResponse(token, user.getEmail(), user.getFullName(), role);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(userDetails);
        String role = user.getRoles().iterator().next().name();

        return new AuthResponse(token, user.getEmail(), user.getFullName(), role);
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    // ===== ADDRESS METHODS =====

    public List<AddressDto> getAddresses(String email) {
        User user = getCurrentUser(email);
        return addressRepository.findByUserId(user.getId()).stream()
                .map(this::toAddressDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDto addAddress(String email, AddressDto dto) {
        User user = getCurrentUser(email);

        // Check if user already has 4 addresses
        long addressCount = addressRepository.findByUserId(user.getId()).size();
        if (addressCount >= 4) {
            throw new AppException("Maximum 4 addresses allowed. Please delete an existing address first.", HttpStatus.BAD_REQUEST);
        }

        Address address = Address.builder()
                .user(user)
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .pinCode(dto.getPinCode())
                .isDefault(dto.isDefault())
                .country(dto.getCountry() != null ? dto.getCountry() : "India")
                .build();

        // If this address is set as default, unset others
        if (dto.isDefault()) {
            List<Address> userAddresses = addressRepository.findByUserId(user.getId());
            userAddresses.forEach(addr -> addr.setDefault(false));
        } else if (addressRepository.findByUserId(user.getId()).isEmpty()) {
            // If it's the first address, make it default
            address.setDefault(true);
        }

        return toAddressDto(addressRepository.save(address));
    }

    // CHANGED: Added update address method
    @Transactional
    public AddressDto updateAddress(String email, Long addressId, AddressDto dto) {
        User user = getCurrentUser(email);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException("Address not found", HttpStatus.NOT_FOUND));

        // Verify address belongs to the user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new AppException("Address not found for this user", HttpStatus.NOT_FOUND);
        }

        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPinCode(dto.getPinCode());
        address.setCountry(dto.getCountry() != null ? dto.getCountry() : "India");
        address.setDefault(dto.isDefault());

        // If this address is set as default, unset others
        if (dto.isDefault()) {
            List<Address> userAddresses = addressRepository.findByUserId(user.getId());
            userAddresses.forEach(addr -> {
                if (!addr.getId().equals(addressId)) {
                    addr.setDefault(false);
                }
            });
        }

        return toAddressDto(addressRepository.save(address));
    }

    // CHANGED: Added delete address method
    @Transactional
    public void deleteAddress(String email, Long addressId) {
        User user = getCurrentUser(email);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException("Address not found", HttpStatus.NOT_FOUND));

        // Verify address belongs to the user
        if (!address.getUser().getId().equals(user.getId())) {
            throw new AppException("Address not found for this user", HttpStatus.NOT_FOUND);
        }

        addressRepository.delete(address);
    }

    // ===== USER UPDATE =====

    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    // ===== PRIVATE HELPERS =====

    private AddressDto toAddressDto(Address a) {
        AddressDto dto = new AddressDto();
        dto.setId(a.getId());
        dto.setStreet(a.getStreet());
        dto.setCity(a.getCity());
        dto.setState(a.getState());
        dto.setPinCode(a.getPinCode());
        dto.setDefault(a.isDefault());
        dto.setCountry(a.getCountry() != null ? a.getCountry() : "India");
        return dto;
    }
}
package com.ecommerce.user.service;

import com.ecommerce.user.dto.AddressDTO;
import com.ecommerce.user.dto.UserRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.model.Address;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserResponse> fetchAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public void addUser(UserRequest userRequest) {
        userRepository.save(mapToUser(userRequest));
    }

    public Optional<UserResponse> fetchUser(Long id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponse);
    }

    public boolean updateUser(Long id, UserRequest updatedUserRequest) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setFirstName(updatedUserRequest.getFirstName());
                    user.setLastName(updatedUserRequest.getLastName());
                    userRepository.save(user);
                    return true;
                }).orElse(false);
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse.UserResponseBuilder userResponseBuilder = UserResponse.builder()
                .id(user.getId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole());

        Address address = user.getAddress();
        if (address != null) {
            AddressDTO addressDTO = AddressDTO.builder()
                    .state(address.getState())
                    .city(address.getCity())
                    .country(address.getCountry())
                    .zipcode(address.getZipcode())
                    .street(address.getStreet())
                    .build();
            userResponseBuilder.address(addressDTO);
        }
        return userResponseBuilder.build();
    }

    private User mapToUser(UserRequest userRequest) {
        User.UserBuilder userBuilder = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .phone(userRequest.getPhone())
                .email(userRequest.getEmail());
        AddressDTO addressDTO = userRequest.getAddress();
        if (addressDTO != null) {
            Address address = Address.builder()
                    .street(addressDTO.getStreet())
                    .state(addressDTO.getState())
                    .zipcode(addressDTO.getZipcode())
                    .country(addressDTO.getCountry())
                    .city(addressDTO.getCity())
                    .build();
            userBuilder.address(address);
        }
        return userBuilder.build();
    }
}

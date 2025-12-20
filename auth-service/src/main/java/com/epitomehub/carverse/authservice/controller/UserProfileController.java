package com.epitomehub.carverse.authservice.controller;

import com.epitomehub.carverse.authservice.dto.UserProfileDto;
import com.epitomehub.carverse.authservice.entity.User;
import com.epitomehub.carverse.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserProfileController {


    private final UserRepository userRepository;
    @GetMapping("/{id}")
    public UserProfileDto getUser(@PathVariable Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        return new UserProfileDto(
                u.getId(),
                u.getFullName(),
                u.getEmail()
        );
    }
}

package com.example.UserService.controller;

import com.example.UserService.dtos.UserRequestDTO;
import com.example.UserService.model.Users;
import com.example.UserService.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // Constructor injection (recommended)
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/addUser")
    public ResponseEntity<Users> addUser(@RequestBody @Valid UserRequestDTO dto) {
        Users savedUser = userService.addUser(dto);

        if (savedUser != null) {
            // return 201 Created with the saved user
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        }

        // 400 Bad Request, no body
        return ResponseEntity.badRequest().build();
    }
}

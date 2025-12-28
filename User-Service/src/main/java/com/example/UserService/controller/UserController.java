package com.example.UserService.controller;

import com.example.UserService.dtos.UserRequestDTO;
import com.example.UserService.dtos.UserSecurityDTO;
import com.example.UserService.model.Users;
import com.example.UserService.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/userDetails")
    public UserSecurityDTO getUserDetails(@RequestParam("contact") String contact){
        Users user = userService.loadUserByUsername(contact);
        System.out.println("User Details logged: "+user);

        return new UserSecurityDTO(
                user.getContact(),
                user.getPassword(),
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList(),
                user.isAccountNonExpired(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired(),
                user.isEnabled()
        );
    }
}

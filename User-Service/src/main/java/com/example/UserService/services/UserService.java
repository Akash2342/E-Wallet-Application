package com.example.UserService.services;

import com.example.UserService.dtos.UserRequestDTO;
import com.example.UserService.model.Users;
import com.example.UserService.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${user.Authority}")
    private String userAuthority;

    @Value("${admin.Authority}")
    private String adminAuthority;

    public Users addUser(@Valid UserRequestDTO dto) {
        Users user = dto.toUser();
        user.setAuthorities(userAuthority);

        // CHECK if user already exists
        Users existing = userRepository.findByContact(user.getContact());

        if (existing != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Save new user

        return userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Users user = userRepository.findByContact(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

}

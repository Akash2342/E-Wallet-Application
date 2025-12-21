package com.example.UserService.services;

import com.example.UserService.dtos.UserRequestDTO;
import com.example.UserService.model.Users;
import com.example.UserService.repository.UserRepository;
import com.example.Utilities.dtos.UserCreatedkafkaDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${user.Authority}")
    private String userAuthority;

    @Value("${admin.Authority}")
    private String adminAuthority;

    public Users addUser(@Valid UserRequestDTO dto) {
        Users user = dto.toUser();
        user.setAuthorities(userAuthority);

        // CHECK if user already exists
        Users existing = userRepository.findByContact(user.getContact());

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Save new user

        Users savedUser = userRepository.save(user);

        // 🔥 Build Kafka JSON
        UserCreatedkafkaDTO event = UserCreatedkafkaDTO.builder()
                .name(savedUser.getName())
                .contact(savedUser.getContact())
                .email(savedUser.getEmail())
                .userIdentifierValue(savedUser.getUserIdentifierValue())
                .userIdentifier(savedUser.getIdentifier().name())
                .build();

        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("USER_CREATED_TOPIC", json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send Kafka message", e);
        }

        return savedUser;
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

package com.example.UserService.repository;

import com.example.UserService.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Users findByContact(String contact);
}

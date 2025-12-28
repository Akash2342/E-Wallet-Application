package com.example.UserService;

import com.example.UserService.model.UserType;
import com.example.UserService.model.Users;
import com.example.UserService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${service.Authority}")
	private String serviceAuthority;

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//comment out this code if this is already created in users db
		Users users = Users.builder().contact("txn-service").
				password(passwordEncoder.encode("txn-service")).
				enabled(true).accountNonLocked(true).credentialsNonExpired(true).accountNonExpired(true).
				email("txnService@gmail.com").authorities(serviceAuthority).
				userType(UserType.SERVICE).
				build();
		userRepository.save(users);

	}

}

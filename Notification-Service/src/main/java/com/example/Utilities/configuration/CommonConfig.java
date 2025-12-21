package com.example.Utilities.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class CommonConfig {

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public SimpleMailMessage getMailMessage(){
        return new SimpleMailMessage();
    }
}

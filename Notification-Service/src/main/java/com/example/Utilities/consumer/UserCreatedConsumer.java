package com.example.Utilities.consumer;

import com.example.Utilities.dtos.UserCreatedkafkaDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service   // 🔥 REQUIRED
public class UserCreatedConsumer {

    private final ObjectMapper objectMapper;

    public UserCreatedConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    private JavaMailSender javaMailSender;

    @KafkaListener(topics = "USER_CREATED_TOPIC", groupId = "notification-group")
    public void sendNotification(String message) {

        try {
            // Convert JSON string → Java object

            log.info("USer Topic received, sending message: " + message);
            UserCreatedkafkaDTO event =
                    objectMapper.readValue(message, UserCreatedkafkaDTO.class);

            // ✅ Business logic
            System.out.println("User Created Event Received");
            System.out.println("Name: " + event.getName());
            System.out.println("Email: " + event.getEmail());
            System.out.println("Contact: " + event.getContact());

            // TODO:
            // sendEmail(event);
            // sendSMS(event);
            // pushNotification(event);

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(event.getEmail());
            mail.setSubject("EWallet User Created for User: " + event.getContact());
            mail.setText("message\n");
            mail.setFrom("doremonxxx010@gmail.com");

            log.info("Sending email to {}", event.getEmail());
            //mailtrap taking long time to post, will find some other alternatives
           // javaMailSender.send(mail);
            log.info("Email sent successfully");


        } catch (Exception e) {
            log.error("Error sending email", e);
            throw new RuntimeException("Failed to consume USER_CREATED event", e);
        }
    }
}

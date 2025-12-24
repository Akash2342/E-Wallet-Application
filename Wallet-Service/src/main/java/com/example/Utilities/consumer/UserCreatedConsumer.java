package com.example.Utilities.consumer;

import com.example.Utilities.dtos.UserCreatedkafkaDTO;
import com.example.Utilities.dtos.WalletCreatedKafkaDTO;
import com.example.Utilities.model.Wallet;
import com.example.Utilities.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class UserCreatedConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${user.creation.time.balance}")
    private double balance;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private WalletRepository walletRepository;

    @KafkaListener(topics = "USER_CREATED_TOPIC", groupId = "wallet-group")
    public void createWallet(String message){

        UserCreatedkafkaDTO event =
                objectMapper.readValue(message, UserCreatedkafkaDTO.class);

        Wallet wallet = Wallet.builder().
                contact(event.getContact()).
                userId(event.getUserId()).
                balance(balance).
                build();

        walletRepository.save(wallet);

        log.info("wallet created");


        // 🔥 Build Kafka JSON
        WalletCreatedKafkaDTO walletCreatedKafkaDTO = WalletCreatedKafkaDTO.builder().
                balance(balance).
                userId(wallet.getUserId()).
                build();

        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("WALLET_CREATED_TOPIC", json);

            log.info("produced kafka message for wallet notification");
        } catch (Exception e) {
            log.error("failed to send kafka message for wallet notification");
            throw new RuntimeException("Failed to send Kafka message", e);
        }



    }

}

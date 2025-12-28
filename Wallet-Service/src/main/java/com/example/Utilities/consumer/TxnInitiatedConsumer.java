package com.example.Utilities.consumer;

import com.example.Utilities.dtos.TxnInitiatedKafkaDTO;
import com.example.Utilities.dtos.TxnUpdatedKafkaDTO;
import com.example.Utilities.model.Wallet;
import com.example.Utilities.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class TxnInitiatedConsumer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final WalletRepository walletRepository;

    public TxnInitiatedConsumer(ObjectMapper objectMapper,
                                KafkaTemplate<String, String> kafkaTemplate,
                                WalletRepository walletRepository) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.walletRepository = walletRepository;
    }

    @KafkaListener(
            topics = "TXN_INITIATED_TOPIC",
            groupId = "wallet-group"
    )
    public void updateWallet(String msg){

        // ✅ Deserialize message → DTO
        TxnInitiatedKafkaDTO txn =
                objectMapper.readValue(msg, TxnInitiatedKafkaDTO.class);

        log.info("Txn initiated event received: {}", txn);

        String sender = txn.getSender();
        String receiver = txn.getReceiver();
        Double amount = txn.getAmount();
        String txnId = txn.getTxnId();

        Wallet senderWallet = walletRepository.findByContact(sender);
        Wallet receiverWallet = walletRepository.findByContact(receiver);

        String message;
        String status;

        if (senderWallet == null) {
            message = "Sender wallet is not associated with us";
            status = "FAILED";

        } else if (receiverWallet == null) {
            message = "Receiver wallet is not associated with us";
            status = "FAILED";

        } else if (amount > senderWallet.getBalance()) {
            message = "Insufficient balance in sender wallet";
            status = "FAILED";

        } else {
            walletRepository.updateWallet(sender, -amount);
            walletRepository.updateWallet(receiver, amount);
            message = "Transaction successful";
            status = "SUCCESS";
        }

        // ✅ Build response DTO
        TxnUpdatedKafkaDTO response = TxnUpdatedKafkaDTO.builder()
                .txnId(txnId)
                .status(status)
                .message(message)
                .build();

        // ✅ Serialize & publish
        kafkaTemplate.send(
                "TXN_UPDATED_TOPIC",
                objectMapper.writeValueAsString(response)
        );

        log.info("Txn update event published for txnId={}", txnId);
    }
}

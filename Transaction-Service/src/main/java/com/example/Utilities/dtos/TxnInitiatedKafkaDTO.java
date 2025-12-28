package com.example.Utilities.dtos;

import com.example.Utilities.model.TxnStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TxnInitiatedKafkaDTO {

    private String txnId;
    private Double amount;
    private String sender;
    private String receiver;
    private TxnStatus status;
    private String purpose;
}
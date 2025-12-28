package com.example.Utilities.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TxnUpdatedKafkaDTO {

    private String txnId;
    private String status;
    private String message;
}

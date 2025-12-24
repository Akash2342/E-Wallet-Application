package com.example.Utilities.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletCreatedKafkaDTO {

    private Integer userId;
    private String contact;
    private Double balance;
}

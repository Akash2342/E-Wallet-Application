package com.example.Utilities.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatedkafkaDTO {

    private String name;
    private String contact;
    private String email;
    private String userIdentifierValue;
    private String userIdentifier;
}

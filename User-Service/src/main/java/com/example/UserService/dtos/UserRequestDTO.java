package com.example.UserService.dtos;

import com.example.UserService.model.UserIdentifier;
import com.example.UserService.model.Users;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import com.example.UserService.model.UserType;
import lombok.*;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
//@RequiredArgsConstructor
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    private String name;

    @NotBlank(message = "contact can not be blank")
    private String contact;

    @NotBlank(message = "email can not be blank")
    private String email;

    private String address;

    private String dob;

    @NotNull(message = "userIdentifier can not be blank")
    private UserIdentifier userIdentifier;

    @NotBlank(message = "userIdentifierValue can not be blank")
    private String userIdentifierValue;

    @NotBlank(message = "password can not be blank")
    private String password;

    public Users toUser() {
        return Users.builder()
                .name(this.name)
                .contact(this.contact)
                .email(this.email)
                .address(this.address)
                .dob(this.dob)
                .identifier(this.userIdentifier)
                .userIdentifierValue(this.userIdentifierValue)
                .password(this.password)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .userType(UserType.USER)
                .build();
    }

}

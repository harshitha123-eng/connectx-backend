package com.connectx.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    private String fullName;

    private String username;

    private String email;

    private String password;

}
package com.ect.Auth_Service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequest {

    private String token;
    private String newPassword;
    private String confirmPassword;

}

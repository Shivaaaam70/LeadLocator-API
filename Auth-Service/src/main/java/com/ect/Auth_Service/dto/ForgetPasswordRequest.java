package com.ect.Auth_Service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForgetPasswordRequest {

    private String email;
}

package com.ect.Auth_Service.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T>{

    private String message;

    private T token;


}

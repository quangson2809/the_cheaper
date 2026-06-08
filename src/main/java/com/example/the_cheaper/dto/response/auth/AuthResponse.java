package com.example.the_cheaper.dto.response.auth;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String accessToken;
    private String refreshToken;
}

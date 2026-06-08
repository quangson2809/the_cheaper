package com.example.the_cheaper.dto.response.admin;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAccountResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;
}


package com.example.the_cheaper.interfaces.rest.dto.response.admin;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private int rewardPoint;
    private String role;
}

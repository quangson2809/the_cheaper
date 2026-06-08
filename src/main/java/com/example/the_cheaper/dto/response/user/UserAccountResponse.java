package com.example.the_cheaper.dto.response.user;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccountResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private int rewardPoint;
    private LocalDateTime createdAt;
    private List<UserAddressResponse> addresses;
}



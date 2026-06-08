package com.example.the_cheaper.dto.response.user;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOptionValueResponse {
    private Long id;
    private String value;
}

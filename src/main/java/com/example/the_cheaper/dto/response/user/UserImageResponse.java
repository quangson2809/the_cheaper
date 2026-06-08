package com.example.the_cheaper.dto.response.user;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserImageResponse {
    private String name;
    private String alt;
}


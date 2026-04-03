package com.example.the_cheaper.interfaces.rest.dto.response.user;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String accountName;
    private String content;
    private LocalDateTime createdAt;
}

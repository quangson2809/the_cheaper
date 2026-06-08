package com.example.the_cheaper.dto.response.user;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewResponse {
    private Long id;
    private Long productId;
    private String accountName;
    private String content;
    private int rating;
    private LocalDateTime createdAt;
}


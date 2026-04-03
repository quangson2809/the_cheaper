package com.example.the_cheaper.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long id;
    private Long accountId;
    private Long productId;
    private String content;
    private int rating;
    private LocalDateTime createdAt;
}

package com.example.the_cheaper.interfaces.rest.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotBlank(message = "Content is required")
    private String content;
}

package com.example.the_cheaper.dto.response.user;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOptionAttributeResponse {
    private Long id;
    private String name;
    private List<UserOptionValueResponse> values;
}

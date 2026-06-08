package com.example.the_cheaper.dto.request.user;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateCartItemRequest {

    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    private int quantity;
}

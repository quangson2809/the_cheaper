package com.example.the_cheaper.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateProfileRequest {
    @NotBlank(message = "Tên không được để trống")
    private String name;

    private String phone;
}

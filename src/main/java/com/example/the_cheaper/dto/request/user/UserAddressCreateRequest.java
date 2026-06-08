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
public class UserAddressCreateRequest {
    @NotBlank(message = "Full name cannot be blank")
    private String fullName;

    @NotBlank(message = "Phone number cannot be blank")
    private String phone;

    @NotBlank(message = "Street cannot be blank")
    private String street;

    @NotBlank(message = "Ward cannot be blank")
    private String ward;

    @NotBlank(message = "District cannot be blank")
    private String district;

    @NotBlank(message = "City cannot be blank")
    private String city;

    private boolean isDefault;
}

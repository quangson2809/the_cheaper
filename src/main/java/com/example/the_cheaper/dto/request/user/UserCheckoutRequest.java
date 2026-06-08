package com.example.the_cheaper.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCheckoutRequest {
    @NotBlank(message = "Receiver name is required")
    private String receiver;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // COD, VNPAY, MOMO
}


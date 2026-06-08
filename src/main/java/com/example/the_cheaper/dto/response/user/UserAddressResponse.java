package com.example.the_cheaper.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressResponse {
    private Long id;
    private String homeNumber;
    private String street;
    private String district;
    private String city;
    private boolean isDefault;
}

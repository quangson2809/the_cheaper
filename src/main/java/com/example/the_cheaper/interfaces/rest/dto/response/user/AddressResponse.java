package com.example.the_cheaper.interfaces.rest.dto.response.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    private Long id;
    private String location;
    private String receiver;
    private String phoneNumber;
}

package com.example.the_cheaper.domain.model;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Long orderId;
    private Money amount;
    private String method;
    private String status;
}

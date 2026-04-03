package com.example.the_cheaper.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Password {
    private String value;

    public boolean match(String password) {
        return this.value.equals(password);
    }

    public void changePassword(String newPassword) {
        this.value = newPassword;
    }

}

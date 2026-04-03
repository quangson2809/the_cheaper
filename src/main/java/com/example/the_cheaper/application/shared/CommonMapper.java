package com.example.the_cheaper.application.shared;

import com.example.the_cheaper.domain.model.Email;
import com.example.the_cheaper.domain.model.Money;
import com.example.the_cheaper.domain.model.Password;
import com.example.the_cheaper.domain.model.PhoneNumber;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CommonMapper {

    public BigDecimal map(Money money) {
        return money != null ? money.getAmount() : null;
    }

    public Money map(BigDecimal amount) {
        return amount != null ? new Money(amount) : null;
    }

    public String map(Email email) {
        return email != null ? email.getValue() : null;
    }

    public Email mapToEmail(String value) {
        return value != null ? new Email(value) : null;
    }

    public String map(PhoneNumber phoneNumber) {
        return phoneNumber != null ? phoneNumber.getValue() : null;
    }

    public PhoneNumber mapToPhoneNumber(String value) {
        return value != null ? new PhoneNumber(value) : null;
    }

    public String map(Password password) {
        return password != null ? password.getValue() : null;
    }

    public Password mapToPassword(String value) {
        return value != null ? new Password(value) : null;
    }
}

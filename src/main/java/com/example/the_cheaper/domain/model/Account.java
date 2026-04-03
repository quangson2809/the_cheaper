package com.example.the_cheaper.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long id;
    private String name;
    private Email email;
    private PhoneNumber phone;
    private Password passwordHash;
    private LocalDateTime createdAt;
    private int rewardPoint;
    private Role role;
    private List<Address> addresses;

    public void addRewardPoint(int points) {
        this.rewardPoint += points;
    }

    public boolean useRewardPoint(int points) {
        if (this.rewardPoint >= points) {
            this.rewardPoint -= points;
            return true;
        }
        return false;
    }

    public void addAddress(Address address) {
        if (addresses == null)
            addresses = new ArrayList<Address>();
        addresses.add(address);
    }

    public void removeAddress(Long addressId) {
        addresses.removeIf(addr -> addr.getId().equals(addressId));
    }

}

package com.example.the_cheaper.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "account_roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_role",
                        columnNames = {"account_id", "role_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;
}

package com.example.the_cheaper.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "reward_point")
    private int rewardPoint;

    private int status = 1;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    private String refreshToken;

    /**
     * Temporary non-persistent builder compatibility for the existing seed data.
     * Authorization must use accountRoles only.
     */
    @Transient
    @Deprecated
    private RoleEntity role;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AccountRoleEntity> accountRoles = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AddressEntity> addresses = new ArrayList<>();

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CartEntity cart;

    public boolean isActive() {
        return this.status == 1;
    }

    public void activate() {
        this.status = 1;
    }

    public void deactivate() {
        this.status = 0;
    }

    public void toggleStatus() {
        this.status = isActive() ? 0 : 1;
    }

    public void updateProfile(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void changePasswordHash(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public void addRole(RoleEntity role) {
        if (role == null || accountRoles.stream()
                .anyMatch(accountRole -> accountRole.getRole().getId().equals(role.getId()))) {
            return;
        }

        accountRoles.add(AccountRoleEntity.builder()
                .account(this)
                .role(role)
                .build());
    }

    public void clearRoles() {
        accountRoles.clear();
    }
}

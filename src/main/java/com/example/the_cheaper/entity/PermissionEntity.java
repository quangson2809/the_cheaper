package com.example.the_cheaper.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "permissions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_permission_name", columnNames = "name"),
                @UniqueConstraint(name = "uk_permission_code", columnNames = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    private String description;
}

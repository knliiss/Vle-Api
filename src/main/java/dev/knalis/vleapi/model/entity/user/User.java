package dev.knalis.vleapi.model.entity.user;

import jakarta.persistence.*;
import lombok.Data;


@Entity(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    private String password;

    private String avatarUrl;

    // Full name (ФИО)
    private String fio;

    @Enumerated(EnumType.STRING)
    private Role role;

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (this.username != null) this.username = this.username.trim().toLowerCase();
    }

}

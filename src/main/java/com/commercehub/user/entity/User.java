package com.commercehub.user.entity;

import com.commercehub.user.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_phone_number", columnList = "phone_number"),
        @Index(name = "idx_user_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntityAudit {

    @Column(nullable = false, length = 200)
    private String firstName;

    @Column(nullable = false, length = 200)
    private String lastName;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(nullable = false, length = 72)
    private String password;

    @Column(length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;
}

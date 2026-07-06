package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Integer userId;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "USER_ROLE")
    private String role;

    @Column(name = "PROFILE_IMAGE_URL")
    private String profileImageUrl;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "ADDRESS")
    private String address;

    // ─── New fields added in Phase 1 ────────────────────────────

    @Column(name = "EMAIL_VERIFIED")
    private Boolean emailVerified = false;

    @Column(name = "STATUS")
    private String status = "ACTIVE"; // ACTIVE, SUSPENDED, BANNED

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "LAST_LOGIN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = new Date();
        }
        if (status == null) {
            status = "ACTIVE";
        }
        if (emailVerified == null) {
            emailVerified = false;
        }
    }

//    @OneToMany(mappedBy = "user")
//    private List<Cart> cart;

}

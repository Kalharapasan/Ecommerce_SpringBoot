package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@Table(name = "STORE")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STORE_ID")
    private Integer storeId;

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User owner;

    @Column(name = "STORE_NAME", nullable = false)
    private String storeName;

    @Column(name = "STORE_DESCRIPTION", length = 1000)
    private String storeDescription;

    @Column(name = "STORE_LOGO_URL")
    private String storeLogoUrl;

    @Column(name = "STORE_BANNER_URL")
    private String storeBannerUrl;

    @Column(name = "STORE_POLICIES", length = 2000)
    private String storePolicies;

    @Column(name = "IS_VERIFIED")
    private Boolean isVerified = false;

    @Column(name = "FOLLOWERS_COUNT")
    private Integer followersCount = 0;

    @Column(name = "RATING")
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "REVIEWS_COUNT")
    private Integer reviewsCount = 0;

    @Column(name = "CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @PrePersist
    protected void onCreate() {
        if (createdDate == null) {
            createdDate = new Date();
        }
        if (isVerified == null) {
            isVerified = false;
        }
        if (followersCount == null) {
            followersCount = 0;
        }
        if (rating == null) {
            rating = BigDecimal.ZERO;
        }
        if (reviewsCount == null) {
            reviewsCount = 0;
        }
    }
}

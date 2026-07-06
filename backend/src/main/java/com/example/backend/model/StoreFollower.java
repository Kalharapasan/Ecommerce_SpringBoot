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
@Table(name = "STORE_FOLLOWER", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"USER_ID", "STORE_ID"})
})
public class StoreFollower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOLLOWER_ID")
    private Integer followerId;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "STORE_ID", nullable = false)
    private Store store;

    @Column(name = "FOLLOWED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date followedDate;

    @PrePersist
    protected void onCreate() {
        if (followedDate == null) {
            followedDate = new Date();
        }
    }
}

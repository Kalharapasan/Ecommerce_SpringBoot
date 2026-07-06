package com.example.backend.model;

import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@Table(name = "MESSAGE")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID")
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "CONTENT", length = 1000)
    private String content;

    @Column(name = "REPLY", length = 1000)
    private String reply;

    @Column(name = "STATUS")
    private String status; // "OPEN", "REPLIED"

    @Column(name = "CREATED_DATE")
    private Date createdDate;
}

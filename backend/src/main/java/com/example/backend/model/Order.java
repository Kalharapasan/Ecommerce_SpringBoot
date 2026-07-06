package com.example.backend.model;

import lombok.*;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "CREATED_DATE")
    private Date createdDate;

    @Column(name = "TOTAL_PRICE")
    private Double totalPrice;

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod;

    @Column(name = "ADDRESS")
    private String address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}

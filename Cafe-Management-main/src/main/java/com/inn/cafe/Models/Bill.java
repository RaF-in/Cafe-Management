package com.inn.cafe.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name="BILL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private Date billDate;
    @ManyToOne
    private User user;
    @OneToMany
    private List<Product> productList;
    private Long totalAmount;
    private String paymentMethod;
}

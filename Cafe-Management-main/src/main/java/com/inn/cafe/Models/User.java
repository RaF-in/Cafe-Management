package com.inn.cafe.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@Table(name="User")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User  {
    private static final Long serialVersionUID = 1L;
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private String username;
    @NonNull
    private String contactNumber;
    @NonNull
    private String email;
    @NonNull
    private String password;
    @NonNull
    private String roles;
    private String status;
    @OneToMany(mappedBy = "user")
    private List<Bill> bill;
    @OneToOne(mappedBy = "user")
    private OneTimePassword oneTimePassword;
}

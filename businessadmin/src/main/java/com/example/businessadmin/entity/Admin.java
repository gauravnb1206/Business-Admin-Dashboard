package com.example.businessadmin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private  boolean active=true;
}

package com.kelompok2.remindertugas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phoneNumber;
    private String chatId = null;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;
}

package com.kelompok2.remindertugas.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subject;
    private String description;
    private String reminderDateTime;
    @Enumerated(EnumType.STRING)
    private ReminderStatus status;
    private String statusDescription;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;

    public enum ReminderStatus {
        PENDING,
        COMPLETED,
        ERROR
    }
}
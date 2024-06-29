package com.kelompok2.remindertugas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MappingContactReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contacts;

    @ManyToOne
    @JoinColumn(name = "reminder_id")
    private Reminder reminders;
}

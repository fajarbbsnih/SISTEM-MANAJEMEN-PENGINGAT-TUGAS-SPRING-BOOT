package com.kelompok2.remindertugas.repository;

import com.kelompok2.remindertugas.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findAllByUsersId(Long userId);
    void deleteByIdAndUsersId(Long id, Long userId);
    List<Reminder> findByReminderDateTime(String reminderDateTime);
}
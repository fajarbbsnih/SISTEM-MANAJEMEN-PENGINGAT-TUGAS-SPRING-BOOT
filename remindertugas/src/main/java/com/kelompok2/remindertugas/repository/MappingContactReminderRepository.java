package com.kelompok2.remindertugas.repository;

import com.kelompok2.remindertugas.entity.MappingContactReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MappingContactReminderRepository extends JpaRepository<MappingContactReminder, Long> {
    void deleteByRemindersId(Long reminderId);
    List<MappingContactReminder> findAllByRemindersIdAndContactsUsersId(Long reminderId, Long userId);
}

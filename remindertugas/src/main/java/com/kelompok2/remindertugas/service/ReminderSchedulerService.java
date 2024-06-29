package com.kelompok2.remindertugas.service;

import com.kelompok2.remindertugas.constanta.ReminderConstants;
import com.kelompok2.remindertugas.entity.MappingContactReminder;
import com.kelompok2.remindertugas.entity.Reminder;
import com.kelompok2.remindertugas.repository.MappingContactReminderRepository;
import com.kelompok2.remindertugas.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderSchedulerService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private TelegramService telegramService;

    @Autowired
    private MappingContactReminderRepository mappingContactReminderRepository;

    @Scheduled(cron = "0 * * * * *")
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        String formattedTime = now.format(ReminderConstants.DATE_TIME_FORMATTER);
        List<Reminder> reminders = reminderRepository.findByReminderDateTime(formattedTime);
        for (Reminder reminder : reminders) {
            String message = "Reminder: \n" + reminder.getSubject() + "\n\n" + reminder.getDescription();
            List<MappingContactReminder> contacts = mappingContactReminderRepository.findAllByRemindersIdAndContactsUsersId(reminder.getId(), reminder.getUsers().getId());

            boolean allSuccess = true;
            LocalDateTime start = LocalDateTime.now();
            for (MappingContactReminder contact : contacts) {
                if (contact.getContacts().getChatId() != null) {
                    try {
                        telegramService.sendMessage(contact.getContacts().getChatId(), message);
                    } catch (Exception e) {
                        allSuccess = false;
                        String errorMessage = e.getMessage();
                        if (errorMessage.length() > 255) {
                            errorMessage = errorMessage.substring(0, 255);
                        }
                        reminder.setStatus(Reminder.ReminderStatus.valueOf("ERROR"));
                        reminder.setStatusDescription("Error: " + errorMessage);
                        break;
                    }
                }
            }
            LocalDateTime end = LocalDateTime.now();
            Duration duration = Duration.between(start, end);
            long seconds = duration.getSeconds();

            if (allSuccess) {
                reminder.setStatus(Reminder.ReminderStatus.valueOf("COMPLETED"));
                reminder.setStatusDescription("Berhasil dieksekusi dengan durasi " + seconds + " detik");
            } else if (reminder.getStatus() == null) {
                reminder.setStatus(Reminder.ReminderStatus.valueOf("ERROR"));
                reminder.setStatusDescription("Error tidak teridentifikasi");
            }

            reminderRepository.save(reminder);
        }
    }
}

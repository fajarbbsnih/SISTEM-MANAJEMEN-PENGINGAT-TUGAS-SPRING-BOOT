package com.kelompok2.remindertugas.service;

import com.kelompok2.remindertugas.constanta.ReminderConstants;
import com.kelompok2.remindertugas.dto.out.DefaultResponse;
import com.kelompok2.remindertugas.entity.Reminder;
import com.kelompok2.remindertugas.entity.Reminder.ReminderStatus;
import com.kelompok2.remindertugas.entity.User;
import com.kelompok2.remindertugas.entity.Contact;
import com.kelompok2.remindertugas.repository.ReminderRepository;
import com.kelompok2.remindertugas.repository.UserRepository;
import com.kelompok2.remindertugas.repository.ContactRepository;
import com.kelompok2.remindertugas.repository.MappingContactReminderRepository;
import com.kelompok2.remindertugas.entity.MappingContactReminder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private MappingContactReminderRepository reminderContactRepository;

    @Transactional
    public DefaultResponse save(Reminder request, Long userId, String reminderDateTimeStr, List<Long> contactIds) {
        if (reminderDateTimeStr == null || !isValidIsoDateTimeFormat(reminderDateTimeStr)) {
            return new DefaultResponse("Format tanggal tidak valid", false);
        }

        LocalDateTime reminderDateTime = LocalDateTime.parse(reminderDateTimeStr, DateTimeFormatter.ISO_DATE_TIME);

        if (reminderDateTime.isBefore(LocalDateTime.now())) {
            return new DefaultResponse("Waktu pengingat tidak valid", false);
        }

        if (request.getSubject() == null || request.getSubject().isBlank() ||
                request.getDescription() == null || request.getDescription().isBlank()) {
            return new DefaultResponse("Mata Kuliah dan deskripsi harus diisi", false);
        }

        String formattedTime = reminderDateTime.format(ReminderConstants.DATE_TIME_FORMATTER);

        request.setReminderDateTime(formattedTime);
        request.setStatus(ReminderStatus.PENDING);

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return new DefaultResponse("User tidak ditemukan", false);
        }
        request.setUsers(userOptional.get());

        for (Long contactId : contactIds) {
            Optional<Contact> contactOptional = contactRepository.findById(contactId);
            if (contactOptional.isEmpty() || !contactOptional.get().getUsers().getId().equals(userId)) {
                return new DefaultResponse("Kontak tidak valid", false);
            }
        }

        Reminder savedReminder = reminderRepository.save(request);
        for (Long contactId : contactIds) {
            MappingContactReminder reminderContact = new MappingContactReminder();
            reminderContact.setReminders(savedReminder);
            reminderContact.setContacts(contactRepository.findById(contactId).get());
            reminderContactRepository.save(reminderContact);
        }

        return new DefaultResponse("Pengingat berhasil disimpan", true);
    }

    @Transactional(readOnly = true)
    public DefaultResponse findAllByUserId(Long userId) {
        List<Reminder> reminders = reminderRepository.findAllByUsersId(userId);
        return new DefaultResponse("Pengingat berhasil diambil", true, reminders);
    }

    @Transactional
    public DefaultResponse deleteByIdAndUserId(Long reminderId, Long userId) {
        Optional<Reminder> reminderOptional = reminderRepository.findById(reminderId);
        if (reminderOptional.isEmpty()) {
            return new DefaultResponse("Pengingat tidak ditemukan", false);
        }

        Reminder reminder = reminderOptional.get();
        if (!reminder.getUsers().getId().equals(userId)) {
            return new DefaultResponse("Tidak memiliki izin untuk menghapus pengingat ini", false);
        }

        if (reminder.getStatus() == ReminderStatus.COMPLETED) {
            return new DefaultResponse("Pengingat yang sudah selesai tidak bisa dihapus", false);
        }

        reminderContactRepository.deleteByRemindersId(reminderId);

        reminderRepository.deleteByIdAndUsersId(reminderId, userId);
        return new DefaultResponse("Pengingat berhasil dihapus", true);
    }

    private boolean isValidIsoDateTimeFormat(String dateStr) {
        try {
            DateTimeFormatter.ISO_DATE_TIME.parse(dateStr);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}

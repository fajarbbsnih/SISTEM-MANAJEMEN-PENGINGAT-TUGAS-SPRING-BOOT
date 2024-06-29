package com.kelompok2.remindertugas.controller;

import com.kelompok2.remindertugas.dto.in.ReminderRequest;
import com.kelompok2.remindertugas.dto.out.DefaultResponse;
import com.kelompok2.remindertugas.entity.Reminder;
import com.kelompok2.remindertugas.entity.User;
import com.kelompok2.remindertugas.service.ReminderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reminders")
@CrossOrigin(origins = "*")
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @PostMapping
    public DefaultResponse createReminder(@RequestBody ReminderRequest reminderRequest, HttpServletRequest request) {
        Long userId = Long.valueOf((String) request.getAttribute("userId"));
        Reminder reminder = new Reminder();
        reminder.setUsers(new User());
        reminder.getUsers().setId(userId);
        reminder.setSubject(reminderRequest.getSubject());
        reminder.setDescription(reminderRequest.getDescription());

        return reminderService.save(reminder, userId, reminderRequest.getReminderDateTimeStr(), reminderRequest.getContactIds());
    }

    @GetMapping
    public DefaultResponse getAllReminders(HttpServletRequest request) {
        Long userId = Long.valueOf((String) request.getAttribute("userId"));
        return reminderService.findAllByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public DefaultResponse deleteReminder(@PathVariable Long id, HttpServletRequest request) {
        Long userId = Long.valueOf((String) request.getAttribute("userId"));
        return reminderService.deleteByIdAndUserId(id, userId);
    }
}
package com.kelompok2.remindertugas.dto.in;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReminderRequest {
    private String subject;
    private String description;
    private String reminderDateTimeStr;
    private List<Long> contactIds;
}

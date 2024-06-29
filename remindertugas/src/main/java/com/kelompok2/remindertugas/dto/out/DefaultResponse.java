package com.kelompok2.remindertugas.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefaultResponse {
    private String message;
    private boolean success;
    private Object data;

    public DefaultResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}

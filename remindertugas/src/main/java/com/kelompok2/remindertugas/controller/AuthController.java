package com.kelompok2.remindertugas.controller;

import com.kelompok2.remindertugas.dto.out.DefaultResponse;
import com.kelompok2.remindertugas.service.AuthService;
import com.kelompok2.remindertugas.util.annotations.ValidateToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public DefaultResponse login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        return authService.login(username, password);
    }

    @ValidateToken
    @PostMapping("/change-password")
    public DefaultResponse changePassword(@RequestBody Map<String, String> passwordRequest, HttpServletRequest request) {
        Long userId = Long.valueOf((String) request.getAttribute("userId"));
        String oldPassword = passwordRequest.get("oldPassword");
        String newPassword = passwordRequest.get("newPassword");
        return authService.changePassword(userId, oldPassword, newPassword);
    }
}

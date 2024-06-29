package com.kelompok2.remindertugas.service;

import com.kelompok2.remindertugas.dto.out.DefaultResponse;
import com.kelompok2.remindertugas.dto.out.LoginResponse;
import com.kelompok2.remindertugas.entity.User;
import com.kelompok2.remindertugas.repository.UserRepository;
import com.kelompok2.remindertugas.util.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DefaultResponse login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                String token = Token.generateToken(user);
                return new DefaultResponse("Login Sukses", true, new LoginResponse(token, user.getUsername(), user.getRoles().getRoleName()));
            }
        }
        return new DefaultResponse("Invalid username or password", false);
    }

    public DefaultResponse changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return new DefaultResponse("Password berhasil diganti", true);
            } else {
                return new DefaultResponse("Password lama salah", false);
            }
        } else {
            return new DefaultResponse("User tidak ditemukan", false);
        }
    }
}
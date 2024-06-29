package com.kelompok2.remindertugas.service;

import com.kelompok2.remindertugas.constanta.RoleConstants;
import com.kelompok2.remindertugas.dto.out.DefaultResponse;
import com.kelompok2.remindertugas.entity.Role;
import com.kelompok2.remindertugas.entity.User;
import com.kelompok2.remindertugas.repository.RoleRepository;
import com.kelompok2.remindertugas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DefaultResponse save(User request) {
        if (userRepository.findByUsername(request.getUsername()) != null || userRepository.findByPhoneNumber(request.getPhoneNumber()) != null) {
            return new DefaultResponse("Username atau nomor telepon sudah ada", false);
        }
        request.setPassword(passwordEncoder.encode("123456"));
        Optional<Role> role = roleRepository.findById(request.getRoles().getId());
        if (role.isEmpty()) {
            return new DefaultResponse("Role tidak ditemukan", false);
        }

        userRepository.save(request);
        return new DefaultResponse("User berhasil disimpan", true);
    }

    public DefaultResponse findAll() {
        List<User> users = userRepository.findAll()
                .stream()
                .filter(user -> !user.getName().equalsIgnoreCase(RoleConstants.ADMIN))
                .collect(Collectors.toList());
        return new DefaultResponse("User berhasil diambil", true, users);
    }

    public DefaultResponse findById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> new DefaultResponse("User berhasil diambil", true, value)).
                orElseGet(() -> new DefaultResponse("User tidak ditemukan", false));
    }

    public DefaultResponse update(User request) {
        Optional<User> existingUser = userRepository.findById(request.getId());
        if (existingUser.isEmpty()) {
            return new DefaultResponse("User tidak ditemukan", false);
        }

        User userWithSameUsername = userRepository.findByUsername(request.getUsername());
        User userWithSamePhoneNumber = userRepository.findByPhoneNumber(request.getPhoneNumber());

        if ((userWithSameUsername != null && !userWithSameUsername.getId().equals(request.getId())) ||
                (userWithSamePhoneNumber != null && !userWithSamePhoneNumber.getId().equals(request.getId()))) {
            return new DefaultResponse("Username atau nomor telepon sudah ada", false);
        }

        Optional<Role> role = roleRepository.findById(request.getRoles().getId());
        if (role.isEmpty()) {
            return new DefaultResponse("Role tidak ditemukan", false);
        }

        User user = existingUser.get();
        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRoles(request.getRoles());

        userRepository.save(user);
        return new DefaultResponse("User berhasil diperbarui", true);
    }

    public DefaultResponse deleteById(Long userId) {
        if (!userRepository.existsById(userId)) {
            return new DefaultResponse("User tidak ditemukan", false);
        }

        userRepository.deleteById(userId);
        return new DefaultResponse("User berhasil dihapus", true);
    }
}

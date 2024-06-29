package com.kelompok2.remindertugas.service;

import com.kelompok2.remindertugas.dto.out.DefaultResponse;
import com.kelompok2.remindertugas.entity.Role;
import com.kelompok2.remindertugas.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kelompok2.remindertugas.constanta.RoleConstants;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public DefaultResponse findAll() {
        List<Role> roles = roleRepository.findAll()
                .stream()
                .filter(role -> !role.getRoleName().equalsIgnoreCase(RoleConstants.ADMIN))
                .collect(Collectors.toList());
        return new DefaultResponse("Daftar peran berhasil diambil", true, roles);
    }
}

package com.kelompok2.remindertugas.controller;

import com.kelompok2.remindertugas.dto.out.DefaultResponse;
import com.kelompok2.remindertugas.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = "*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public DefaultResponse getAllRoles(HttpServletRequest request) {
        return roleService.findAll();
    }
}

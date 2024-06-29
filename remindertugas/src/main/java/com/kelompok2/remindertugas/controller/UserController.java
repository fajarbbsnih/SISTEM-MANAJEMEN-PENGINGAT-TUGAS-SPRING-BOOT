package com.kelompok2.remindertugas.controller;

import com.kelompok2.remindertugas.dto.out.DefaultResponse;
import com.kelompok2.remindertugas.entity.User;
import com.kelompok2.remindertugas.service.UserService;
import com.kelompok2.remindertugas.util.annotations.ValidateToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @ValidateToken
    @PostMapping
    public DefaultResponse createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @ValidateToken
    @GetMapping
    public DefaultResponse getAllUsers() {
        return userService.findAll();
    }

    @ValidateToken
    @GetMapping("/{id}")
    public DefaultResponse getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @ValidateToken
    @PutMapping("/{id}")
    public DefaultResponse updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.update(user);
    }

    @ValidateToken
    @DeleteMapping("/{id}")
    public DefaultResponse deleteUser(@PathVariable Long id) {
        return userService.deleteById(id);
    }
}

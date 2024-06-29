package com.kelompok2.remindertugas.controller;

import com.kelompok2.remindertugas.dto.out.DefaultResponse;
import com.kelompok2.remindertugas.entity.Contact;
import com.kelompok2.remindertugas.entity.User;
import com.kelompok2.remindertugas.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/contacts")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public DefaultResponse createContact(@RequestBody Contact contact, HttpServletRequest request) {
        Long userId = Long.valueOf((String) request.getAttribute("userId"));
        contact.setUsers(new User());
        contact.getUsers().setId(userId);
        return contactService.save(contact);
    }

    @GetMapping
    public DefaultResponse getAllContacts(HttpServletRequest request) {
        Long userId = Long.valueOf((String) request.getAttribute("userId"));
        return contactService.findAllByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public DefaultResponse deleteContact(@PathVariable Long id, HttpServletRequest request) {
        Long userId = Long.valueOf((String) request.getAttribute("userId"));
        return contactService.deleteByIdAndUserId(id, userId);
    }
}

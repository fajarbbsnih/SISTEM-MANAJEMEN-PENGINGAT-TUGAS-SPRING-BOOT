package com.kelompok2.remindertugas.service;

import com.kelompok2.remindertugas.dto.out.DefaultResponse;
import com.kelompok2.remindertugas.entity.Contact;
import com.kelompok2.remindertugas.repository.ContactRepository;
import com.kelompok2.remindertugas.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    public DefaultResponse save(Contact request) {
        if (contactRepository.findByPhoneNumberAndUsersId(request.getPhoneNumber(), request.getUsers().getId()) != null) {
            return new DefaultResponse("Kontak sudah ada", false);
        }

        contactRepository.save(request);
        return new DefaultResponse("Kontak berhasil disimpan", true);
    }

    public DefaultResponse findAllByUserId(Long userId) {
        List<Contact> contacts = contactRepository.findAllByUsersId(userId);
        return new DefaultResponse("Daftar kontak berhasil diambil", true, contacts);
    }

    @Transactional
    public DefaultResponse deleteByIdAndUserId(Long contactId, Long userId) {
        if (!contactRepository.existsById(contactId)) {
            return new DefaultResponse("Kontak tidak ditemukan", false);
        }

        contactRepository.deleteByIdAndUsersId(contactId, userId);
        return new DefaultResponse("Kontak berhasil dihapus", true);
    }
}
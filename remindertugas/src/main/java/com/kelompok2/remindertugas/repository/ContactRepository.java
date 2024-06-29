package com.kelompok2.remindertugas.repository;

import com.kelompok2.remindertugas.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findAllByUsersId(Long userId);
    void deleteByIdAndUsersId(Long contactId, Long userId);
    Contact findByPhoneNumberAndUsersId(String phoneNumber, Long userId);
    Optional<Contact> findByPhoneNumber(String phoneNumber);
}
package com.kelompok2.remindertugas.config;

import com.kelompok2.remindertugas.constanta.RoleConstants;
import com.kelompok2.remindertugas.entity.Role;
import com.kelompok2.remindertugas.entity.User;
import com.kelompok2.remindertugas.repository.RoleRepository;
import com.kelompok2.remindertugas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataLoader {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            if (roleRepository.findById(1L).isEmpty()) {
                Role adminRole = new Role();
                adminRole.setRoleName(RoleConstants.ADMIN);
                roleRepository.save(adminRole);
            }

            if (roleRepository.findById(2L).isEmpty()) {
                Role leaderRole = new Role();
                leaderRole.setRoleName(RoleConstants.LEADER);
                roleRepository.save(leaderRole);
            }

            if (userRepository.findByUsername("admin") == null) {
                Optional<Role> adminRole = roleRepository.findById(1L);
                if (adminRole.isPresent()) {
                    User adminUser = new User();
                    adminUser.setName("admin");
                    adminUser.setUsername("admin");
                    adminUser.setPassword(passwordEncoder.encode("admin"));
                    adminUser.setPhoneNumber("-");
                    adminUser.setRoles(adminRole.get());
                    userRepository.save(adminUser);
                }
            }
        };
    }
}

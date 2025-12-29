package com.example.businessadmin.config;

import com.example.businessadmin.entity.Admin;
import com.example.businessadmin.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner createAdmin(AdminRepository repo,
                                  PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("admin").isEmpty()) {
                repo.save(
                        Admin.builder()
                                .username("admin")
                                .password(encoder.encode("admin123"))
                                .active(true)
                                .build()
                );
            }
        };
    }
}

package com.example.businessadmin.security;

import com.example.businessadmin.entity.Admin;
import com.example.businessadmin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Username not found"));

        return new User(
                admin.getUsername(),
                admin.getPassword(),
                admin.isActive(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))

        );
    }
}

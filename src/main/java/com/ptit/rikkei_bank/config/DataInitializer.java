package com.ptit.rikkei_bank.config;
import java.util.List;
import java.util.ArrayList;

import com.ptit.rikkei_bank.entity.Role;
import com.ptit.rikkei_bank.entity.User;
import com.ptit.rikkei_bank.repository.RoleRepository;
import com.ptit.rikkei_bank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize Roles
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN", "Quản trị viên")));
        
        Role staffRole = roleRepository.findByName("STAFF")
                .orElseGet(() -> roleRepository.save(new Role(null, "STAFF", "Nhân viên ngân hàng")));

        roleRepository.findByName("CUSTOMER")
                .orElseGet(() -> roleRepository.save(new Role(null, "CUSTOMER", "Khách hàng")));

        // Initialize Default Admin User
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhoneNumber("0999999999");
            admin.setEmail("admin@rikkeibank.com");
            admin.setIsActive(true);
            admin.setIsKyc(true);
            admin.setRole(adminRole);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);
            log.info("[DataInitializer] Default admin user created! (Username: admin / Password: admin123)");
        }

        // Initialize Default Staff User
        if (!userRepository.existsByUsername("staff")) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setPassword(passwordEncoder.encode("staff123"));
            staff.setPhoneNumber("0888888888");
            staff.setEmail("staff@rikkeibank.com");
            staff.setIsActive(true);
            staff.setIsKyc(true);
            staff.setRole(staffRole);
            staff.setCreatedAt(LocalDateTime.now());
            userRepository.save(staff);
            log.info("[DataInitializer] Default staff user created! (Username: staff / Password: staff123)");
        }
        // Generate 50 random mock users for pagination testing
        if (userRepository.count() == 0) {
            log.info("[DataInitializer] Generating 50 mock users for pagination testing...");
            List<User> mockUsers = new ArrayList<>();
            Role customerRole = roleRepository.findByName("CUSTOMER").orElse(null);
            if (customerRole != null) {
                for (int i = 1; i <= 50; i++) {
                    User u = new User();
                    u.setUsername("mockuser" + i);
                    u.setPassword(passwordEncoder.encode("password123"));
                    u.setPhoneNumber("0" + (100000000 + i));
                    u.setEmail("mockuser" + i + "@rikkeibank.com");
                    u.setIsActive(true);
                    u.setIsKyc(false);
                    u.setIsDeleted(false);
                    u.setRole(customerRole);
                    u.setCreatedAt(LocalDateTime.now().minusDays(i % 30));
                    mockUsers.add(u);
                }
                userRepository.saveAll(mockUsers);
                log.info("[DataInitializer] Successfully generated 100 mock users!");
            }
        }
    }
}

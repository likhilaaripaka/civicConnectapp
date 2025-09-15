package com.civicconnect.config;

import com.civicconnect.model.User;
import com.civicconnect.model.UserRole;
import com.civicconnect.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create sample admin user if not exists
            if (userRepository.findByEmail("admin@civicconnect.com").isEmpty()) {
                User admin = new User(
                        "Admin User",
                        "admin@civicconnect.com",
                        passwordEncoder.encode("admin123"), // ✅ encode password
                        UserRole.ADMIN,
                        "Downtown"
                );
                userRepository.save(admin);
                System.out.println("Sample admin user created: admin@civicconnect.com / admin123");
            }

            // Create sample citizen user if not exists
            if (userRepository.findByEmail("citizen@example.com").isEmpty()) {
                User citizen = new User(
                        "John Citizen",
                        "citizen@example.com",
                        passwordEncoder.encode("citizen123"), // ✅ encode password
                        UserRole.CITIZEN,
                        "North District"
                );
                userRepository.save(citizen);
                System.out.println("Sample citizen user created: citizen@example.com / citizen123");
            }
        };
    }
}

package com.miapp.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestPasswordEncoder implements CommandLineRunner {

    @Override
    public void run(String... args) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        String hash = encoder.encode("admin123");
        System.out.println("====== HASH ADMIN123 ======");
        System.out.println(hash);
    }
}

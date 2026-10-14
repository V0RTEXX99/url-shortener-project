package com.aayaniqbal.spring_boot_url_shortener;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtility {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("for secret: " + encoder.encode("secret"));
        System.out.println("for admin: " + encoder.encode("admin"));
    }
}

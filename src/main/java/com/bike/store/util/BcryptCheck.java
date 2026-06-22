package com.bike.store.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptCheck {
    public static void main(String[] args) {
        // Replace these values if needed
        /*String raw = "Admin@123";
        String hash = "$2a$10$OETc3h9xIVJkAC9EJvN5.eUzwQtVf8gYjfKvuZqR/NxqYQ5pBPbzy";

        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        System.out.println("raw:   " + raw);
        System.out.println("hash:  " + hash);
        System.out.println("matches: " + enc.matches(raw, hash));*/

        String raw = "Admin@123";
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        System.out.println(enc.encode(raw));
    }
}

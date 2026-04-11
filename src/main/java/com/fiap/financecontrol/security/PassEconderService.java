package com.fiap.financecontrol.security;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PassEconderService  {

    private final PasswordEncoder passwordEncoder;

    public PassEconderService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    public String hash(String senhaPura) {
        return passwordEncoder.encode(senhaPura) ;
    }


    public boolean matches(String senhaPura, String senhaHash) {
        return passwordEncoder.matches(senhaPura, senhaHash);
    }
}
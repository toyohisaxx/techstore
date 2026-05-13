package com.techstore.techstore.controller;

import com.techstore.techstore.dto.LoginRequest;
import com.techstore.techstore.dto.LoginResponse;
import com.techstore.techstore.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final String USERNAME = "admin@techstore.cl";
    private final String PASSWORD = "Admin1234";

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        if (USERNAME.equals(request.getUsername())
                && PASSWORD.equals(request.getPassword())) {

            String token = JwtUtil.generarToken(request.getUsername());

            return new LoginResponse(
                    token,
                    "Bearer",
                    "3600"
            );
        }

        throw new RuntimeException("Credenciales incorrectas");
    }
}
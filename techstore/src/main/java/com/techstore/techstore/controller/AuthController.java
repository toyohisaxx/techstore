package com.techstore.techstore.controller;

import com.techstore.techstore.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final String USERNAME = "admin";
    private final String PASSWORD = "1234";

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> datos) {

        String username = datos.get("username");
        String password = datos.get("password");

        if (USERNAME.equals(username) && PASSWORD.equals(password)) {

            String token = JwtUtil.generarToken(username);

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("token", token);

            return respuesta;
        }

        throw new RuntimeException("Credenciales incorrectas");
    }
}
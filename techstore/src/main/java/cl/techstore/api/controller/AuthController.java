package cl.techstore.api.controller;

import cl.techstore.api.dto.LoginRequest;
import cl.techstore.api.dto.LoginResponse;
import cl.techstore.api.security.JwtUtil;

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
package com.flowgym.crud.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    // Endpoint para obter as informações do usuário autenticado
    @GetMapping("/current-user")
    public Map<String, String> getCurrentUser(Authentication authentication) {
        Map<String, String> response = new HashMap<>();

        // Verifica se o usuário está autenticado
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // Nome do usuário
            String role = authentication.getAuthorities().stream()
                                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                                        .findFirst().orElse("UNKNOWN");

            // Retorna o nome do usuário e a role (papel)
            response.put("username", username);
            response.put("role", role);
        } else {
            response.put("error", "Usuário não autenticado");
        }

        return response;
    }
}

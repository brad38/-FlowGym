package com.flowgym.crud.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowgym.crud.model.AlunoLoginModel;
import com.flowgym.crud.model.AlunoModel;
import com.flowgym.crud.repositories.AlunoRepository;
import com.flowgym.crud.repositories.LoginRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    @Autowired
    AlunoRepository aRepository;

    @Autowired
    LoginRepository loginRepository; // Adicione a injeção do LoginRepository

    // Endpoint para obter as informações do usuário autenticado
    @GetMapping("/current-user")
    public Map<String, String> getCurrentUser(Authentication authentication) {
        Map<String, String> response = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); 
            String role = authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .findFirst().orElse("UNKNOWN");

            // 1. Busca o usuário pelo username no LoginRepository
            Optional<AlunoLoginModel> usuarioLoginOptional = loginRepository.findByUsername(username);

            if (usuarioLoginOptional.isPresent()) {
                // 2. Obtém a matrícula do usuário
                String matricula = usuarioLoginOptional.get().getUsername(); 

                // 3. Busca o aluno pela matrícula no AlunoRepository
                Optional<AlunoModel> alunoOptional = aRepository.findByMatricula(matricula);

                if (alunoOptional.isPresent()) {
                    AlunoModel aluno = alunoOptional.get();
                    String cpf = aluno.getCpf();

                    response.put("username", username);
                    response.put("role", role);
                    response.put("cpf", cpf);
                } else {
                    response.put("error", "Aluno não encontrado");
                }
            } else {
                response.put("error", "Usuário não encontrado"); 
            }
        } else {
            response.put("error", "Usuário não autenticado");
        }

        return response;
    }

}

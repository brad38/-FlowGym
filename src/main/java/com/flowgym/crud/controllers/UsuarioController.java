package com.flowgym.crud.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowgym.crud.dtos.CpfLoginDto;
import com.flowgym.crud.model.AlunoLoginModel;
import com.flowgym.crud.model.AlunoModel;
import com.flowgym.crud.model.InstrutorModel;
import com.flowgym.crud.repositories.AlunoRepository;
import com.flowgym.crud.repositories.InstrutorRepository;
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
    LoginRepository loginRepository;

    @Autowired
    InstrutorRepository instrutorRepository; // Adicione a injeção do InstrutorRepository

    @Autowired
    private PasswordEncoder passwordEncoder;

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
                AlunoLoginModel usuarioLogin = usuarioLoginOptional.get();

                // 2. Verifica a role do usuário
                if (role.equals("ROLE_ALUNO")) {
                    // 3a. Se for aluno, busca os dados do aluno
                    Optional<AlunoModel> alunoOptional = aRepository.findByMatricula(username);
                    if (alunoOptional.isPresent()) {
                        AlunoModel aluno = alunoOptional.get();
                        response.put("username", username);
                        response.put("role", role);
                        response.put("cpf", aluno.getCpf());
                    } else {
                        response.put("error", "Aluno não encontrado");
                    }
                } else if (role.equals("ROLE_INSTRUTOR")) {
                    // 3b. Se for instrutor, busca os dados do instrutor
                    Optional<InstrutorModel> instrutorOptional = instrutorRepository.findByCpf(username);
                    if (instrutorOptional.isPresent()) {
                        InstrutorModel instrutor = instrutorOptional.get();
                        response.put("username", username);
                        response.put("role", role);
                        response.put("cpf", instrutor.getCpf());
                    } else {
                        response.put("error", "Instrutor não encontrado");
                    }
                } else {
                    // 3c. Se for outra role (admin, recepcionista), retorna apenas username e role
                    response.put("username", username);
                    response.put("role", role);
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
package com.flowgym.crud.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowgym.crud.model.AlunoLoginModel;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<AlunoLoginModel, String> {
    Optional<AlunoLoginModel> findByUsername(String username);  // Encontrar usuário pela matrícula
}

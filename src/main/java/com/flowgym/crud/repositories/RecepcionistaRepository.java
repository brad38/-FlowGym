package com.flowgym.crud.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowgym.crud.model.RecepcionistaModel;

public interface RecepcionistaRepository extends JpaRepository<RecepcionistaModel, Integer> {
    Optional<RecepcionistaModel> findByEmail(String email);
    Optional<RecepcionistaModel> findByTelefone(String telefone);  
    Optional<RecepcionistaModel> findByCpf(String cpf);
}

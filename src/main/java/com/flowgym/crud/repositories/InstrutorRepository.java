package com.flowgym.crud.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowgym.crud.model.InstrutorModel;

public interface InstrutorRepository extends JpaRepository<InstrutorModel, Integer>{
    Optional<InstrutorModel> findByEmail(String email);
    Optional<InstrutorModel> findByTelefone(String telefone);  
    Optional<InstrutorModel> findByCpf(String cpf);
}

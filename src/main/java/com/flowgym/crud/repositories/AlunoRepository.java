package com.flowgym.crud.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.flowgym.crud.model.AlunoModel;

public interface AlunoRepository extends JpaRepository<AlunoModel, Integer>{
    Optional<AlunoModel> findByEmail(String email);
    Optional<AlunoModel> findByTelefone(String telefone);  
    Optional<AlunoModel> findByCpf(String cpf);
    @Query("SELECT MAX(a.matricula) FROM Aluno a") //Busca a maior matricula no banco de dados
    String findMaxMatricula();
    Optional<AlunoModel> findByMatricula(String matricula);
}

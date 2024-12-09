package com.flowgym.crud.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flowgym.crud.model.FichaTreinoModel;

@Repository
public interface FichaTreinoRepository extends JpaRepository<FichaTreinoModel, Integer> {
    Optional<FichaTreinoModel> findByAlunoCpf(String cpf); 
    Optional<FichaTreinoModel> findByAlunoMatricula(String matricula);
    List<FichaTreinoModel> findAllByAlunoMatricula(String matricula);
}

//o "findBy" é uma conversão, ele vai ate a variavel "Aluno" no "FichaTreinoModel" que está associada a tabela "aluno" 
    //(@ManyToOne @JoinColumn(name = "aluno_id") e busca pelo campo em questão
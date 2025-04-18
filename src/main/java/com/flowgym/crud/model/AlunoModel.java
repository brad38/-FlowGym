package com.flowgym.crud.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "Aluno")
@Table(name = "Aluno")
public class AlunoModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nome;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate nascimento;
    
    private String email;

    private String telefone;

    private String cpf;

    private boolean menor;

    @Column(name = "matricula", unique = true, nullable = false)
    private String matricula;

    private String responsavelCpf; // CPF do responsável, sem chave estrangeira

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dataVencimento;
}

package com.flowgym.crud.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public record AlunoDto( //Dto é uma classe que a gente instância o que a gente quer da entidade "AlunoModel",
// entidade essa que é representação das tabelas do nosso banco de dados, e ele ja cria os getters, setters e construtores;

    @NotBlank(message = "É preciso declarar um nome") 
    String nome,

    @NotNull(message = "Data de nascimento não pode ser vazia") 
    @Past(message = "A data de nascimento deve estar no passado")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate nascimento,

    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataVencimento,

    @Email(message = "Email inválido")
    String email,

    @NotBlank (message = "Telefone não pode ser vazio")
    @Pattern(regexp = "\\(\\d{2}\\) \\d{5}-\\d{4}", message = "formato inválido de telefone")
    String telefone,

    @Pattern(regexp = "\\d{11}", message = "CPF inválido")
    String cpf,
    
    boolean menor, // Flag para indicar se é menor ou não
    
    @Pattern(regexp = "\\d{11}", message = "CPF inválido")
    String responsavelCpf, // CPF do responsável, caso seja menor

    String matricula
) { }
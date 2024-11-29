package com.flowgym.crud.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RecepcionistaDto(
    @NotBlank(message = "É preciso declarar um nome")
    String nome,
    
    @NotBlank(message = "É preciso declarar um email")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Telefone não pode ser vazio")
    @Pattern(regexp= "\\(\\d{2}\\) \\d{5}-\\d{4}", message = "formato inválido de telefone")
    String telefone,

    @NotBlank(message = "É preciso declarar um cpf")
    @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", message = "CPF inválido")

    String cpf
) {}

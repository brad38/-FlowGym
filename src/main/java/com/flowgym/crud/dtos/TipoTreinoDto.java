package com.flowgym.crud.dtos;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record TipoTreinoDto (
    @NotBlank 
    String tipo,

    @NotBlank
    List<String> exercicios
){}

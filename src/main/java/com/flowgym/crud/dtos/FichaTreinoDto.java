package com.flowgym.crud.dtos;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FichaTreinoDto(
    
    String matricula,

    @NotBlank
    String objetivo,
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataInicio,

    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataTermino,

    @NotNull
    List<TipoTreinoDto> tipos,

    @Min(0)
    int numeroImpressoes, //Numero de vezes que o treino foi impressa

    int maxImpressoes
) {}

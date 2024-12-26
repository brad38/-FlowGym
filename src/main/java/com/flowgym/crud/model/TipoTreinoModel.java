package com.flowgym.crud.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tipo_treino")
public class TipoTreinoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String tipo;  // Tipo do treino: A, B, C, D, E

    @ManyToOne
    @JoinColumn(name = "ficha_treino_id", nullable = false)
    @JsonBackReference // Evita o loop infinito na serialização que eu estava tendo
    private FichaTreinoModel fichaTreino;  // Relacionamento com a ficha de treino

    @ElementCollection
    @CollectionTable(name = "exercicios_tipo", joinColumns = @JoinColumn(name = "tipo_treino_id"))
    @Column(name = "exercicio")
    private List<String> exercicios; // Lista de exercícios específica para este tipo de treino
}

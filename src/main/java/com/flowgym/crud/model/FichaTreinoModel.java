package com.flowgym.crud.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ficha_treino")
public class FichaTreinoModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false) // Relaciona com o id do AlunoModel
    private AlunoModel aluno;

    @Column(nullable = false)
    private String objetivo; // Exemplo: "Hipertrofia", "Definição"

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dataInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dataTermino;

    @OneToMany(mappedBy = "fichaTreino", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Controla a serialização
    private List<TipoTreinoModel> tiposTreino = new ArrayList<>();

    @Column(name = "max_impressoes", nullable = false)
    private int maxImpressoes = 30;
    
    @Column(name = "numero_impressoes", nullable = false)
    private int numeroImpressoes = 0; // Inicia com 0 por padrão
    
    // Método para incrementar o número de impressões
    public void incrementarImpressao() {
        this.numeroImpressoes++;
    }
    
}




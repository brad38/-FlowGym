package com.flowgym.crud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "Recepcionista")
@Table(name = "Recepcionista")
public class RecepcionistaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nome;

    private String email;

    private String telefone;

    private String cpf;
}

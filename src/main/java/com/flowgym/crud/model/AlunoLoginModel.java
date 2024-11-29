package com.flowgym.crud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "Usuario")
@Table(name = "Usuario")
public class AlunoLoginModel {
    @Id
    private String username;
    private String password;
    private String role;  

}

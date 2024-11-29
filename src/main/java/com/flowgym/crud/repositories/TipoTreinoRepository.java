package com.flowgym.crud.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flowgym.crud.model.TipoTreinoModel;

    public interface TipoTreinoRepository extends JpaRepository<TipoTreinoModel, Integer> {

}

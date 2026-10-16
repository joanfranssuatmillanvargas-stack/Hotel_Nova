package com.hotelnova.habitaciones.repositorio;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hotelnova.habitaciones.modelo.Temporada;

public interface temporada_repositorio extends MongoRepository<Temporada, String> {

	List<Temporada> findByTipoId(String tipoId);

	List<Temporada> findAllByOrderByInicioAsc();

	long countByTipoId(String tipoId);
}

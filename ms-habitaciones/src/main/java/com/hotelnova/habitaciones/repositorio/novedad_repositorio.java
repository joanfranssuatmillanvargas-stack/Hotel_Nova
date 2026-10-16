package com.hotelnova.habitaciones.repositorio;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hotelnova.habitaciones.modelo.Novedad;

public interface novedad_repositorio extends MongoRepository<Novedad, String> {

	List<Novedad> findByHabitacionIdOrderByFechaDesc(String habitacionId);

	List<Novedad> findAllByOrderByFechaDesc();
}

package com.hotelnova.habitaciones.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hotelnova.habitaciones.modelo.TipoHabitacion;

public interface tipo_habitacion_repositorio extends MongoRepository<TipoHabitacion, String> {

	Optional<TipoHabitacion> findByNombreIgnoreCase(String nombre);

	List<TipoHabitacion> findAllByOrderByTarifaBaseAsc();
}

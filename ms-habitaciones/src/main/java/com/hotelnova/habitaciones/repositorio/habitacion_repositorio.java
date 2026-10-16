package com.hotelnova.habitaciones.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hotelnova.habitaciones.modelo.EstadoHabitacion;
import com.hotelnova.habitaciones.modelo.Habitacion;

public interface habitacion_repositorio extends MongoRepository<Habitacion, String> {

	List<Habitacion> findAllByOrderByNumeroAsc();

	List<Habitacion> findByEstadoOrderByNumeroAsc(EstadoHabitacion estado);

	Optional<Habitacion> findByNumero(String numero);

	boolean existsByNumero(String numero);

	long countByTipoId(String tipoId);

	long countByEstado(EstadoHabitacion estado);
}

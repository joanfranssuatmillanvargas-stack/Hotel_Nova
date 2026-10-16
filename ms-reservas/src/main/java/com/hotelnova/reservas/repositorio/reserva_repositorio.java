package com.hotelnova.reservas.repositorio;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hotelnova.reservas.modelo.EstadoReserva;
import com.hotelnova.reservas.modelo.Reserva;

public interface reserva_repositorio extends MongoRepository<Reserva, String> {

	List<Reserva> findAllByOrderByLlegadaAsc();

	List<Reserva> findByHabitacionIdAndEstadoIn(String habitacionId, Collection<EstadoReserva> estados);

	List<Reserva> findByEstadoAndLlegada(EstadoReserva estado, LocalDate llegada);

	long countByHuespedDocumentoAndEstado(String huespedDocumento, EstadoReserva estado);
}

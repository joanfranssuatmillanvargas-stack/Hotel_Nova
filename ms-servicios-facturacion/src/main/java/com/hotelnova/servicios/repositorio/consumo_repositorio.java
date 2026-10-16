package com.hotelnova.servicios.repositorio;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hotelnova.servicios.modelo.Consumo;

public interface consumo_repositorio extends MongoRepository<Consumo, String> {

	List<Consumo> findByReservaIdOrderByFechaAsc(String reservaId);

	boolean existsByServicioId(String servicioId);
}

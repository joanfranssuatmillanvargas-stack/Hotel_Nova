package com.hotelnova.servicios.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hotelnova.servicios.modelo.Factura;

public interface factura_repositorio extends MongoRepository<Factura, String> {

	Optional<Factura> findByReservaId(String reservaId);

	boolean existsByReservaId(String reservaId);

	List<Factura> findAllByOrderByNumeroDesc();
}

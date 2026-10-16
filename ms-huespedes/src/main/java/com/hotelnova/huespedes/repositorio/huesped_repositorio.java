package com.hotelnova.huespedes.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hotelnova.huespedes.modelo.Huesped;

public interface huesped_repositorio extends MongoRepository<Huesped, String> {

	Optional<Huesped> findByDocumento(String documento);

	boolean existsByDocumento(String documento);

	List<Huesped> findAllByOrderByApellidosAscNombresAsc();
}

package com.hotelnova.servicios.repositorio;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hotelnova.servicios.modelo.Servicio;

public interface servicio_repositorio extends MongoRepository<Servicio, String> {

	List<Servicio> findAllByOrderByCategoriaAscNombreAsc();
}

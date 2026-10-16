package com.hotelnova.web.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hotelnova.web.modelo.Usuario;

public interface usuario_repositorio extends MongoRepository<Usuario, String> {

	Optional<Usuario> findByUsuario(String usuario);

	boolean existsByUsuario(String usuario);

	List<Usuario> findAllByOrderByRolAscUsuarioAsc();
}

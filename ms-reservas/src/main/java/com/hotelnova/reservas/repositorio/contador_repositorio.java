package com.hotelnova.reservas.repositorio;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;

/**
 * Genera consecutivos con la colección "contadores" (como una secuencia de SQL).
 * Cada documento es {_id: "nombre", valor: n} y se incrementa de forma atómica.
 */
@Repository
public class contador_repositorio {

	private final MongoTemplate mongo;

	public contador_repositorio(MongoTemplate mongo) {
		this.mongo = mongo;
	}

	public long siguiente(String nombre) {
		Document d = mongo.getCollection("contadores").findOneAndUpdate(Filters.eq("_id", nombre), Updates.inc("valor", 1L),
				new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
		return d == null ? 1 : ((Number) d.get("valor")).longValue();
	}
}

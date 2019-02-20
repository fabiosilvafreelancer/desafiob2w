package br.com.swapi.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.com.swapi.model.Planet;

public interface PlanetRepository extends MongoRepository <Planet, String>{

	List<Planet> findByName(String name);

}

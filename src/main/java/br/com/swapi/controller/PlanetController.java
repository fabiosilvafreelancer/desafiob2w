package br.com.swapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.swapi.exception.PlanetNotFoundException;
import br.com.swapi.model.Planet;
import br.com.swapi.service.PlanetService;

/**
*
* Controller class that encapsulate rest request to api 
* 
* @author fabiosilva
*
*/

@RestController
@RequestMapping("/planets")
public class PlanetController {
	
	@Autowired
	private PlanetService planetService;
	
	@GetMapping
	List<Planet> findAll() throws RestClientException, PlanetNotFoundException {
		return planetService.findAll();
	}

	@GetMapping("/{id}") 
	Planet findById(@PathVariable String id) throws RestClientException, PlanetNotFoundException{
		return planetService.findById(id);
	}

	@GetMapping("/find") 
	Planet findByName(@RequestParam(value="name", required=true) String name) throws RestClientException, PlanetNotFoundException{
		return planetService.findByName(name);
	}

	@PostMapping
	ResponseEntity<?> createPlanet(@RequestBody Planet planet) throws RestClientException {
		Planet newPlanet = planetService.create(planet);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/{id}").buildAndExpand(newPlanet.getId()).toUri();
		return ResponseEntity.created(location).body(newPlanet);		
	}
	
	@PutMapping("/{id}")
	ResponseEntity<?> updatePlanet(@RequestBody Planet planet, String id) {
		Planet updatedPlanet = planetService.update(planet, id);
		return ResponseEntity.ok().body(updatedPlanet);		
	}

	@DeleteMapping ("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void deleteById(@PathVariable String id) {
		planetService.deleteById(id);
	}
}

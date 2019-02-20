package br.com.swapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import br.com.swapi.exception.DuplicatedPlanetException;
import br.com.swapi.exception.NoSuchPlanetInSwapiException;
import br.com.swapi.exception.PlanetNotFoundException;
import br.com.swapi.externals.swapiexternal.model.StarwarsApiPlanet;
import br.com.swapi.externals.swapiexternal.service.SwapiService;
import br.com.swapi.model.Planet;
import br.com.swapi.repository.PlanetRepository;
import br.com.swapi.utils.Messages;

@Service
public class PlanetService {
	
	@Autowired
	private Messages messages;
	
	@Autowired 
	private SwapiService swapiService;
	
	@Autowired
	private PlanetRepository planetRepository;
	
	public Planet create(Planet planet) throws PlanetNotFoundException, RestClientException {
		
		List<Planet> planets = planetRepository.findByName(planet.getName());
		if (planets.size() > 0) {
			Planet p = planets.get(0);
			throw new DuplicatedPlanetException(messages.planetDuplicatedName(p.getName(), p.getId()));
		}
		
		Planet newPlanet = planetRepository.insert(planet);
		
		try {
			StarwarsApiPlanet swapiPlanet = swapiService.getSwapiPlanet(planet.getName());	
			newPlanet.setApparitionsCount(swapiPlanet.getApparitionsCount());
		} catch (NoSuchPlanetInSwapiException e) {
			newPlanet.setApparitionsCount(0);
		}
		
		return newPlanet;
	}
	
	/**
	 * 
	 * @param id
	 * @return a Planet object, if found. Before returning the Planet, it
	 * retrieves the apparitionsCount from SWAPI and adds it to the Planet object
	 * @throws RestClientException 
	 * @throws PlanetNotFoundException when planet not found inside api database
	 * @throws NoSuchPlanetInSwapiException 
	 */
	public Planet findById(String id) throws PlanetNotFoundException, RestClientException {

		Planet planet = planetRepository.findById(id).orElseThrow(() -> new PlanetNotFoundException(messages.planetNotFoundByIdAttribute(id)));
		
		try {
			planet.setApparitionsCount(swapiService.getSwapiPlanet(planet.getName()).getApparitionsCount());
		}
		catch (NoSuchPlanetInSwapiException e) {
			planet.setApparitionsCount(0);
		}
		return planet;
	}

	public Planet findByName(String name) throws PlanetNotFoundException, RestClientException {
		List<Planet> planets = planetRepository.findByName(name);
		if (planets.size() == 0) {
			throw new PlanetNotFoundException(messages.planetNameNotFound(name));
		}
		
		Planet planet = planets.get(0);
		
		try {
			planet.setApparitionsCount(swapiService.getSwapiPlanet(planet.getName()).getApparitionsCount());
		} 
		catch (NoSuchPlanetInSwapiException e) {
			planet.setApparitionsCount(0);
		}
		
		return planet;
	}

	public void deleteById(String id) throws PlanetNotFoundException {
		if (planetRepository.existsById(id)) {
			planetRepository.deleteById(id);
		} else {
			throw new PlanetNotFoundException(messages.planetNotFoundByIdAttribute(id));
		}
	}
	
	public List<Planet> findAll() throws PlanetNotFoundException, RestClientException {
		List<Planet> planets = planetRepository.findAll();
		
		if (planets.size() == 0) {
			throw new PlanetNotFoundException(messages.noSuchPlanetsFound());
		}
		
		for (Planet planet: planets) {
			try {
				StarwarsApiPlanet swapiPlanet = swapiService.getSwapiPlanet(planet.getName());
				planet.setApparitionsCount(swapiPlanet.getApparitionsCount());
			}
			catch (NoSuchPlanetInSwapiException e) {
				planet.setApparitionsCount(0);
			}
		}
		return planets;
	}

	public Planet update(Planet planet, String id) {

		if (planetRepository.existsById(id)) {
			 
			Planet updatePlanet = planetRepository.findById(id).get();
			updatePlanet.setName(planet.getName());
			updatePlanet.setClimate(planet.getClimate());
			updatePlanet.setTerrain(planet.getTerrain());

			try {
				StarwarsApiPlanet swapiPlanet = swapiService.getSwapiPlanet(planet.getName());	
				updatePlanet.setApparitionsCount(swapiPlanet.getApparitionsCount());
			} catch (NoSuchPlanetInSwapiException e) {
				updatePlanet.setApparitionsCount(0);
			}		

			return planetRepository.save(updatePlanet);
			
		} else {
			throw new PlanetNotFoundException(messages.planetNotFoundByIdAttribute(id));
		}
	}

}

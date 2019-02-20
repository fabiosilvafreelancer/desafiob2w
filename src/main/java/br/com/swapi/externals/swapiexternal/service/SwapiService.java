package br.com.swapi.externals.swapiexternal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import br.com.swapi.exception.NoSuchPlanetInSwapiException;
import br.com.swapi.exception.PlanetNotFoundException;
import br.com.swapi.externals.swapiexternal.model.StarwarsApiPlanet;
import br.com.swapi.externals.swapiexternal.model.StarwarsApiSearchResult;
import br.com.swapi.utils.Messages;

@Service
public class SwapiService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${swapi.baseurl}")
	private String SWAPI_BASEURL;	
	
	@Autowired
	private Messages messages;
	
	@Autowired
	private RestTemplate restTemplate;

	@Cacheable("StarwarsApiPlanets")
	public StarwarsApiPlanet getSwapiPlanet(String name) throws PlanetNotFoundException, NoSuchPlanetInSwapiException, RestClientException {		
		
		String url = SWAPI_BASEURL + "/planets?search=" + name;
		logger.info("Executing request to swapi api at " + url + ".");
		logger.info("Param name: " + name + ".");
		
		StarwarsApiSearchResult swapiSearch = restTemplate.getForObject(url, StarwarsApiSearchResult.class);
		
		if (swapiSearch.getCount() == 0) {
			throw new NoSuchPlanetInSwapiException(messages.noSuchPlanetInSwapi(name));
		}		
		
		for (StarwarsApiPlanet swapiPlanet: swapiSearch.getResults()) {
			if (swapiPlanet.getName().equals(name)) {
				return swapiPlanet;
			}
		}
		
		throw new NoSuchPlanetInSwapiException(messages.noSuchPlanetInSwapi(name));
	}	
	
}

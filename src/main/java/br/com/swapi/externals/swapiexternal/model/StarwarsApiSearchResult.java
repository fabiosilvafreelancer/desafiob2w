package br.com.swapi.externals.swapiexternal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StarwarsApiSearchResult {	

	private Integer count;
	private StarwarsApiPlanet[] results;
	
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public StarwarsApiPlanet[] getResults() {
		return results;
	}

	public void setResults(StarwarsApiPlanet[] results) {
		this.results = results;
	}

}

package br.com.swapi.externals.swapiexternal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StarwarsApiPlanet {

	private String name;
	private String[] films;
	
	public String[] getFilms() {
		return films;
	}
	
	public void setFilms(String[] films) {
		this.films = films;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getApparitionsCount() {
		return films == null ? 0 : films.length;
	}
}

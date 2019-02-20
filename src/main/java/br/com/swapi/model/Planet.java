package br.com.swapi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Planet {
	
	@Id 
	private String id;
	
	@Indexed(unique=true)
	private String name;
	
	private String climate;
	private String terrain;
	
	@Transient
	private int apparitionsCount;

	public Planet() {
		
	}

	public Planet(String name, String climate, String terrain) {
		this.name = name;
		this.climate = climate;
		this.terrain = terrain;
	}

	public String getId() {
		return this.id;
	}	

	public String getName() {
		return name;
	}

	public void setName(String firstName) {
		this.name = firstName;
	}

	public String getClimate() {
		return climate;
	}

	public void setClimate(String lastName) {
		this.climate = lastName;
	}

	public String getTerrain() {
		return terrain;
	}

	public void setTerrain(String terrain) {
		this.terrain = terrain;
	}
	
	public int getApparitionsCount() {
		return apparitionsCount;
	}

	public void setApparitionsCount(int apparitionsCount) {
		this.apparitionsCount = apparitionsCount;
	}

	@Override
	public String toString() {
		return "Planet [id=" + id + ", name=" + name + ", climate=" + climate + ", terrain=" + terrain
				+ ", apparitionsCount=" + apparitionsCount + "]";
	}
}

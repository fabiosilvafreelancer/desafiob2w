package br.com.swapi.utils;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class Messages {
	
	@Autowired
	private MessageSource messageSource;
	
	private static Locale locale = Locale.US;
	
	public String noSuchPlanetsFound() {
		Object[] args = new Object[] {};
		return messageSource.getMessage("noSuchPlanetsFound", args, locale);
	}
	
	public String planetNotFoundByIdAttribute(String id) {
		Object[] args = new Object[] {id};
		return messageSource.getMessage("planetNotFoundByIdAttribute", args, locale);
	}
	
	public String planetNameNotFound(String name) {
		Object[] args = new Object[] {name};
		return messageSource.getMessage("planetNameNotFound", args, locale);
	}

	public String planetDuplicatedName(String name, String id) {
		Object[] args = new Object[] {name, id};
		return messageSource.getMessage("planetDuplicatedName", args, locale);
	}	
	
	public String noSuchPlanetInSwapi(String name) {
		Object[] args = new Object[] {name};
		return messageSource.getMessage("noSuchPlanetInSwapi", args, locale);
	}

}

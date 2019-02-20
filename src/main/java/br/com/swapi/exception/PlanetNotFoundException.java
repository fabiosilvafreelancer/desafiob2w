package br.com.swapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * This exception is fired when someone try find a resource, in this case a planet or planet list, 
 * and this resource not exists in api context 
 * 
 * @author fabiosilva
 *
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlanetNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3464574794984213943L;

	public PlanetNotFoundException(String message) {
		super(message);
	}

}

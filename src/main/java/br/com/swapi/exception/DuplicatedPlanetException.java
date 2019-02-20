package br.com.swapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * This exception is fired when someone try create a planet with a registered name to another planet 
 * 
 * @author fabiosilva
 *
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatedPlanetException extends RuntimeException {

	private static final long serialVersionUID = 4153260902281183889L;

	public DuplicatedPlanetException (String msg) {
		super(msg);
	}
}

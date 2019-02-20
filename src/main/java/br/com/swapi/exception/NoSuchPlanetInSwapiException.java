package br.com.swapi.exception;

public class NoSuchPlanetInSwapiException extends Exception {

	private static final long serialVersionUID = 735934426296054887L;

	public NoSuchPlanetInSwapiException(String message) {
		super(message);
	}	
	
}

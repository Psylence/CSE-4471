package edu.osu.AU13.cse4471.securevote;

import java.util.Random;

public class SecretPolynomial {
	private int order;
	private int secret;
	private int[] coefficents;
	
	/**
	 * Create a new polynomial that can be used to encode a secret.
	 * 
	 * @param order The order of the polynomial
	 * @param secret The secret to be enycrpted as a constant
	 */
	public SecretPolynomial(int order, int secret) {
		this.order = order;
		this.secret = secret;
		
		Random rng = new Random();
		coefficents = new int[order];
		for(int i = 0; i < order; i++) {
			coefficents[i] = rng.nextInt();
		}
	}
	
	public SecretPolynomial(SecretPoint[] points) {
		// TODO use the polynomial interpolation algorithm
	}
	
	public SecretPoint getPoint(int x) {
		int sum = secret;
		int currentPow = x;
		for(int i = 0; i < order; i++) {
			sum += coefficents[i] * currentPow;
			currentPow *= x;
		}
		
		return new SecretPoint(x, sum);
	}
	
	
}

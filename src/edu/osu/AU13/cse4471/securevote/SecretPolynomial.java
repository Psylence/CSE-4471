package edu.osu.AU13.cse4471.securevote;

import java.security.SecureRandom;
import java.util.Random;

public class SecretPolynomial {
	private static final double EPSILON = 0.0000001;
	
	private int order;
	private int secret;
	private SecretPoint[] points;
	
	/**
	 * Create a new polynomial that can be used to encode a secret.
	 * 
	 * @param order The order of the polynomial
	 * @param secret The secret to be enycrpted as a constant
	 */
	public SecretPolynomial(int order, int secret) {
		this.order = order;
		this.secret = secret;
		
		// Create some random integer coefficents from which to construct the points
		SecureRandom rng = new SecureRandom();
		int[] coefficents = new int[order];
		for(int i = 0; i < order; i++) {
			coefficents[i] = rng.nextInt();
		}
		
		// Calculate the points
		points = new SecretPoint[order];
		for(int i = 0; i < order; i++) {
			// Calculate the y value of a point
			int sum = secret;
			int curPow = 1;
			for(int j = order - 1; j > 0; j--) {
				sum += curPow * coefficents[j];
				curPow *= (i + 1);
			}
			
			points[i] = new SecretPoint(i + 1, sum);
		}
	}
	
	public SecretPolynomial(SecretPoint[] points) {
		this.points = points;
		order = points.length;
		computeSecret();
	}
	
	/**
	 * Uses interpolation to determine the y intercept.
	 */
	private void computeSecret() {
		
		double sum = 0.0;
		for(int i = 0; i < order; i++) {
			double partialSum = points[i].getY();
			
			for(int j = 0; j < order; j++) {
				if(j == i) continue;
				partialSum *= -points[j].getX();
				partialSum /= (points[i].getX() - points[j].getX());
			}
			
			sum += partialSum;
		}
		
		
		
		secret = (int)sum;
		
		if(sum - secret > EPSILON) 
			throw new IllegalArgumentException("These points do not encode an integer secret.");
	}
	
	public SecretPoint getPoint(int x) {
		if(x < 0 || x >= order) throw new IndexOutOfBoundsException("Must provide a value for x in the range [0, order - 1].");
		return points[x];
	}
	
	public int getSecret() {
		return secret;
	}
}

package edu.osu.AU13.cse4471.securevote;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;

public class SecretPolynomial implements JSONSerializable {
	private static final double EPSILON = 0.0000001;
	
	private static final String JSON_POINTS = "points";
	
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
		
		// This bound on coeffiecent size should guarantee that there is no overflow when used in the voting algorithm
		int bound = (int) (Integer.MAX_VALUE / (Math.pow(order + 1, order + 1)) - 1);
		for(int i = 0; i < order; i++) {
			coefficents[i] = rng.nextInt(bound) + 1;
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

	@Override
	public JSONObject toJson() throws JSONException {
		JSONArray arr = new JSONArray();
		for(SecretPoint p : points) {
			arr.put(p.toJson());
		}
		
		return new JSONObject().put(JSON_POINTS, arr);
	}
	
	public static class Deserializer implements JSONDeserializer<SecretPolynomial> {

		public Deserializer() {}
		
		@Override
		public SecretPolynomial fromJson(JSONObject obj) throws JSONException {
			JSONArray arr = obj.getJSONArray(JSON_POINTS);
			
			ArrayList<SecretPoint> points = JSONUtils.fromArray(arr, new SecretPoint.Deserializer());
			
			return new SecretPolynomial((SecretPoint[]) points.toArray());
		}
		
	}
}

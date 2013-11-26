package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;
import edu.osu.AU13.cse4471.securevote.math.MathUtils;

public class SecretPolynomial implements JSONSerializable {
	private static final String JSON_POINTS = "points";

	private int order;
	private SecretPoint[] points;

	/**
	 * Create a new polynomial that can be used to encode a secret.
	 * 
	 * @param order
	 *            The order of the polynomial
	 * @param secret
	 *            The secret to be enycrpted as a constant
	 */
	public SecretPolynomial(int order, BigInteger coeffBound) {
		this.order = order;

		// Create some random integer coefficents from which to construct the
		// points
		BigInteger[] coefficents = new BigInteger[order + 1];

		// This bound on coeffiecent size should guarantee that there is no
		// overflow when used in the voting algorithm
		for (int i = 0; i <= order; i++) {
			coefficents[i] = MathUtils.randomBigInteger(coeffBound);
		}

		// Calculate the points
		points = new SecretPoint[order + 2];
		for (int i = 0; i <= order + 1; i++) {
			// Calculate the y value of a point
			BigInteger sum = BigInteger.ZERO;
			BigInteger curPow = BigInteger.ONE;
			BigInteger x = BigInteger.valueOf(i);
			for (int j = order; j >= 0; j--) {
				sum = sum.add(curPow.multiply(coefficents[j]));
				curPow = curPow.multiply(x);
			}

			points[i] = new SecretPoint(i, sum);
		}
	}

	public SecretPolynomial(SecretPoint[] points) {
		this.points = points;
		order = points.length - 2;
	}

	public SecretPoint getPoint(int x) {
		if (x <= 0 || x > order + 1) {
			throw new IndexOutOfBoundsException(
					"Must provide a value for x in the range [0, order].");
		}
		return points[x];
	}

	public BigInteger getSecret() {
		return points[0].getY();
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONArray arr = new JSONArray();
		for (SecretPoint p : points) {
			arr.put(p.toJson());
		}

		return new JSONObject().put(SecretPolynomial.JSON_POINTS, arr);
	}

	public static class Deserializer implements
			JSONDeserializer<SecretPolynomial> {

		public Deserializer() {
		}

		@Override
		public SecretPolynomial fromJson(JSONObject obj) throws JSONException {
			JSONArray arr = obj.getJSONArray(SecretPolynomial.JSON_POINTS);

			ArrayList<SecretPoint> points = JSONUtils.fromArray(arr,
					new SecretPoint.Deserializer());

			return new SecretPolynomial((SecretPoint[]) points.toArray());
		}

	}
}

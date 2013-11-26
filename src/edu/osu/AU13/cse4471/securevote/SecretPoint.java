package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;

// TODO change this to use BigIntegers for robustness sake
/**
 * A class for representing components of part of a secret.
 * 
 * @author Alex
 */
public class SecretPoint implements Comparable<SecretPoint>, JSONSerializable {

	private static final String JSON_X = "x";
	private static final String JSON_Y = "y";

	private int x;
	private BigInteger y;

	public SecretPoint(int x, BigInteger y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public BigInteger getY() {
		return y;
	}

	@Override
	public int compareTo(SecretPoint another) {
		return ((Integer) x).compareTo(another.getX());
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();

		obj.put(JSON_X, x);
		obj.put(JSON_Y, y.toString());

		return obj;
	}

	public static class Deserializer implements JSONDeserializer<SecretPoint> {

		public Deserializer() {
		}

		@Override
		public SecretPoint fromJson(JSONObject obj) throws JSONException {
			int x;
			BigInteger y;

			x = Integer.parseInt(obj.getString(JSON_X));
			y = new BigInteger(obj.getString(JSON_Y));

			return new SecretPoint(x, y);
		}

	}
}

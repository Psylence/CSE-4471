package edu.osu.AU13.cse4471.securevote;

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
	private int y;

	public SecretPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
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
		obj.put(JSON_Y, y);

		return obj;
	}

	public static class Deserializer implements JSONDeserializer<SecretPoint> {

		public Deserializer() {
		}

		@Override
		public SecretPoint fromJson(JSONObject obj) throws JSONException {
			int x, y;

			x = Integer.parseInt(obj.getString(JSON_X));
			y = Integer.parseInt(obj.getString(JSON_Y));

			return new SecretPoint(x, y);
		}

	}
}

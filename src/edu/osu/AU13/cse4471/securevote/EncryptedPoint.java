package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;

import org.json.JSONException;
import org.json.JSONObject;

import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;
import edu.osu.AU13.cse4471.securevote.math.Group;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;

public class EncryptedPoint implements JSONSerializable {
	private final static String JSON_X = "x";
	private final static String JSON_Y = "y";
	
	private int x;
	private GroupElement y;
	
	public EncryptedPoint(SecretPoint point, PublicKey key) {
		x = point.getX();
		y = key.encode(new BigInteger("" + point.getY()));
	}
	
	public EncryptedPoint(int x, GroupElement y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public GroupElement getY() {
		return y;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		
		obj.put(JSON_X, x);
		obj.put(JSON_Y, y.toString());
		
		return obj;
	}
	
	public static class Deserializer implements JSONDeserializer<EncryptedPoint> {

		private Group g;
		
		public Deserializer(Group g) {
			this.g = g;
		}
		
		@Override
		public EncryptedPoint fromJson(JSONObject obj) throws JSONException {
			int x = Integer.parseInt(obj.getString(JSON_X));
			GroupElement y = g.elementFromString(obj.getString(JSON_Y));
			return new EncryptedPoint(x, y);
		}
		
	}
}

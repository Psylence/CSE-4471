package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;

import org.json.JSONException;
import org.json.JSONObject;

import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;
import edu.osu.AU13.cse4471.securevote.math.Group;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;

public class PublicKey implements JSONSerializable {
	private static final String JSON_KEY = "key";
	
	private GroupElement key;

	public PublicKey(GroupElement key) {
		this.key = key;
	}

	public GroupElement encode(BigInteger message) {
		return key.exp(message);
	}

	@Override
	public String toString() {
		return key.toString();
	}

	public static PublicKey fromString(Group group, String s) {
		return new PublicKey(group.elementFromString(s));
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();
		
		obj.put(JSON_KEY, key.toString());
		
		return obj;
	}
	
	public static class Deserializer implements JSONDeserializer<PublicKey> {

		private Group g;
		
		public Deserializer(Group g) {
			this.g = g;
		}
		
		@Override
		public PublicKey fromJson(JSONObject obj) throws JSONException {
			GroupElement key;
			
			key = g.elementFromString(obj.getString(JSON_KEY));
			
			return new PublicKey(key);
		}
		
	}
}

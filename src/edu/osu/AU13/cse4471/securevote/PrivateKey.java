package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;

import org.json.JSONException;
import org.json.JSONObject;

import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;
import edu.osu.AU13.cse4471.securevote.math.Group;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;
import edu.osu.AU13.cse4471.securevote.math.MathUtils;

public class PrivateKey implements JSONSerializable {
	private final static String JSON_PRIV = "privateKey";
	private final static String JSON_PUB = "publicKey";
	private final static String JSON_GROUP = "group";

	private BigInteger privateKey;
	private PublicKey publicKey;
	private Group group;

	public PrivateKey(Group group, GroupElement g) {
		// Get a random large integer
		privateKey = MathUtils.randomBigInteger(group.order());

		// Generate the public key
		GroupElement key = group.exponent(g, privateKey);
		publicKey = new PublicKey(key);

	}
	
	public PrivateKey(BigInteger privateKey, PublicKey publicKey, Group group) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
		this.group = group;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public GroupElement decode(GroupElement cypher) {
		return cypher.exp(privateKey.modInverse(group.order()));
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();

		obj.put(JSON_PRIV, privateKey.toString());
		obj.put(JSON_PUB, publicKey.toJson());
		obj.put(JSON_GROUP, group.toString());

		return obj;
	}

	public static class Deserializer implements JSONDeserializer<PrivateKey> {

		public Deserializer() {
		}

		@Override
		public PrivateKey fromJson(JSONObject obj) throws JSONException {
			BigInteger privateKey;
			PublicKey publicKey;
			Group group;
			
			privateKey = new BigInteger(obj.getString(JSON_PRIV));
			group = Group.fromString(obj.getString(JSON_GROUP));
			publicKey = new PublicKey.Deserializer(group).fromJson((obj.getJSONObject(JSON_PUB)));
			
			return new PrivateKey(privateKey, publicKey, group);
		}

	}
}

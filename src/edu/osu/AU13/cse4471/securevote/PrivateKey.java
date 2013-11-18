package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;

import edu.osu.AU13.cse4471.securevote.math.Group;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;
import edu.osu.AU13.cse4471.securevote.math.MathUtils;

public class PrivateKey {
	private BigInteger privateKey;
	private PublicKey publicKey = null;
	private Group group;

	public PrivateKey(Group group, GroupElement g) {
		// Get a random large integer
		privateKey = MathUtils.randomBigInteger(group.order());

		// Generate the public key
		GroupElement key = group.exponent(g, privateKey);
		publicKey = new PublicKey(key);

	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public GroupElement decode(GroupElement cypher) {
		return cypher.exp(privateKey.modInverse(group.order()));
	}
}

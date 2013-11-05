package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;

import edu.osu.AU13.cse4471.securevote.math.Group;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;
import edu.osu.AU13.cse4471.securevote.math.IntegersModM;

public class PublicKey {
	private GroupElement key;
	
	public PublicKey(GroupElement key) {
		this.key = key;
	}
	
	public GroupElement encode(BigInteger message) {
		return key.exp(message);
	}
}

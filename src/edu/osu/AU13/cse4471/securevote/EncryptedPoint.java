package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;

import edu.osu.AU13.cse4471.securevote.math.GroupElement;

public class EncryptedPoint {
	private int x;
	private GroupElement y;
	
	public EncryptedPoint(SecretPoint point, PublicKey key) {
		x = point.getX();
		y = key.encode(new BigInteger("" + point.getY()));
	}
	
	public int getX() {
		return x;
	}
	
	public GroupElement getY() {
		return y;
	}
}

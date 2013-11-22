package edu.osu.AU13.cse4471.securevote;

import java.util.List;
import java.util.Random;

// TODO change this to use BigIntegers for robustness sake
/**
 * A class for representing components of part of a secret.
 * 
 * @author Alex
 */
public class SecretPoint implements Comparable<SecretPoint> {
	
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
		return ((Integer)x).compareTo(another.getX());
	}
	
}

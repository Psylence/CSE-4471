package edu.osu.AU13.cse4471.securevote.math;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Sample implementation of a Group. Note that the integers mod <i>m</i> is
 * <b>NOT</b> a good group for cryptography, since several problems (such as the
 * discrete logarithm problem) are easily solvable. Only use this to see how a
 * concrete subclass of Group "should" look.
 * 
 * 
 * @author andrew
 */
public class IntegersModM extends CyclicGroup {
  private final BigInteger modulus;

  public IntegersModM(BigInteger mod) {
    modulus = mod;
  }
  
  @Override
  public GroupElement getRandomElement() {
    return getRandomElement(modulus);
  }
  
  @Override
  public GroupElement getRandomElement(BigInteger bound) {
    BigInteger result;
    SecureRandom s = new SecureRandom();
    byte[] bytes = new byte[bound.bitLength() / 8 + 1];

    // Bound the most significant byte
    s.nextBytes(bytes);
    if(bound.toByteArray()[0] != Byte.MAX_VALUE)
    	bytes[0] = (byte)s.nextInt(bound.toByteArray()[0]);
    else
    	bytes[0] = 0;
    
    result = new BigInteger(bytes);

    return new Elem(result);
  }

  @Override
  public GroupElement getRandomGenerator() {
	
	BigInteger cand;  
    do {
      cand = getRandomElement().getValue();
    } while (!cand.gcd(modulus).equals(BigInteger.ONE));

    // Guaranteed: cand is coprime to modulus (i.e., the only positive number
    // that divides both cand and modulus is 1
    // Thus, cand generates this group.
    return new Elem(cand);
  }

  @Override
  public GroupElement identity() {
    return new Elem(BigInteger.ZERO);
  }

  @Override
  public GroupElement multiply(GroupElement a, GroupElement b)
      throws IllegalArgumentException {
    if (a.getGroup() != this || b.getGroup() != this) {
      throw new IllegalArgumentException(a + " and " + b
          + " are not members of " + this);
    }

    BigInteger A = ((Elem) a).n, B = ((Elem) b).n;
    return new Elem(A.add(B).mod(modulus));
  }

  @Override
  public GroupElement inverse(GroupElement g) throws IllegalArgumentException {
    if (g.getGroup() != this) {
      throw new IllegalArgumentException(g + " is not a member of " + this);
    }

    BigInteger G = ((Elem) g).n;
    return new Elem(G.negate().mod(modulus));
  }

  @Override
  public GroupElement elementFromString(String s) {
    if (s.startsWith("[") && s.endsWith("]")) {
      try {
        return new Elem(new BigInteger(s.substring(1, s.length() - 1)));
      } catch (NumberFormatException e) {
      }
    }
    throw new IllegalArgumentException(
        "'"
            + s
            + "' is not a valid encoding of a group element in the group of integers mod "
            + modulus);

  }

  @Override
  protected BigInteger[] getParameters() {
    return new BigInteger[] { modulus };
  }

  private class Elem extends GroupElement {
    final BigInteger n;

    Elem(BigInteger n) {
      if (n.compareTo(modulus) > 0 || n.signum() < 0) {
        n = n.mod(modulus);
      }
      this.n = n;
    }

    @Override
    public Group getGroup() {
      return IntegersModM.this;
    }
    
    @Override
    public BigInteger getValue() {
    	return n;
    }

    @Override
    public int hashCode() {
      return n.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof Elem) {
        Elem oo = (Elem) o;
        return getGroup() == oo.getGroup() && n.equals(oo.n);
      } else {
        return false;
      }
    }

    @Override
    public String toString() {
      return "[" + n + "]";
    }
  }
}

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
    if (mod.signum() < 0 || mod.equals(BigInteger.ONE)) {
      throw new IllegalArgumentException(
          "Can't/don't want to consider the group of integers mod " + mod);
    }
    modulus = mod;
  }
  
  @Override
  public GroupElement getRandomElement() {
	BigInteger result;
	SecureRandom s = new SecureRandom();
	byte[] bytes = new byte[modulus.bitLength() * 8 + 1];

    s.nextBytes(bytes);
    result = new BigInteger(bytes);
	
	return new Elem(result);
	  
  }

  @Override
  public GroupElement getRandomGenerator() {
    BigInteger cand;
    SecureRandom s = new SecureRandom();
    byte[] bytes = new byte[modulus.bitLength() * 8 + 1];

    do {
      s.nextBytes(bytes);
      cand = new BigInteger(bytes);
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
  protected BigInteger[] getParameters() {
    return new BigInteger[] { modulus };
  }

  private class Elem extends GroupElement {
    final BigInteger n;

    Elem(BigInteger n) {
      this.n = n;
    }

    @Override
    public Group getGroup() {
      return IntegersModM.this;
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

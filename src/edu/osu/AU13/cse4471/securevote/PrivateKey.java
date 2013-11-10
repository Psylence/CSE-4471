package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;

import edu.osu.AU13.cse4471.securevote.math.CyclicGroup;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;

public class PrivateKey {
  private CyclicGroup group;
  private GroupElement generator;
  private GroupElement privateKey;
  private GroupElement inverse;

  private PublicKey publicKey = null;

  public PrivateKey(CyclicGroup group) {
    this.group = group;

    generator = group.getRandomGenerator();

    // Get a random large integer
    privateKey = group.getRandomElement();

    GroupElement key = null;// group.exponent(generator, privateKey);
    publicKey = new PublicKey(key);

  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public BigInteger decode(GroupElement cypher) {
    return null;
  }
}

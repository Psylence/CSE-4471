package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;
import java.security.SecureRandom;

import edu.osu.AU13.cse4471.securevote.math.CyclicGroup;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;
import edu.osu.AU13.cse4471.securevote.math.IntegersModPrimePower;

public class PrivateKey {
  private GroupElement generator;
  private BigInteger privateKey;

  private PublicKey publicKey = null;

  public PrivateKey(IntegersModPrimePower group) {
    generator = group.getRandomGenerator();

    // Get a random large integer
    privateKey = group.getRandomElement(group.getPrime()).getValue();

    // Generate the public key
    GroupElement key = group.exponent(generator, privateKey);
    publicKey = new PublicKey(key);

  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public BigInteger decode(GroupElement cypher) {
    return cypher.exp(privateKey.negate()).getValue();
  }
}

package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;
import java.security.SecureRandom;

import edu.osu.AU13.cse4471.securevote.math.CyclicGroup;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;

public class PrivateKey {
  private GroupElement generator;
  private BigInteger privateKey;

  private PublicKey publicKey = null;

  public PrivateKey(CyclicGroup group) {
    generator = group.getRandomGenerator();

    // Get a random large integer
    SecureRandom s = new SecureRandom();
    byte[] bytes = new byte[32]; // 32 is arbitrary; need a way to bound this to p
    
    s.nextBytes(bytes);
    privateKey = new BigInteger(bytes);

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

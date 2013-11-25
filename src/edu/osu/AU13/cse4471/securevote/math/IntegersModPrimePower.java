package edu.osu.AU13.cse4471.securevote.math;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * An extension of IntegersModM for representing a more specific type of modulo
 * group
 * 
 * @author Alex
 */
public class IntegersModPrimePower extends IntegersModM {

	private static final int PRIME_TEST = 80;

	private BigInteger prime;
	private int pow;

	/**
	 * Creates a random prime and power and returns the prime power group that
	 * represents them
	 * 
	 * @return <code>generateRandom(64, 16)</code>;
	 */
	public static IntegersModPrimePower generateRandom() {
		return IntegersModPrimePower.generateRandom(64, 16);
	}

	/**
	 * Creates a random prime and power and returns the prime power group that
	 * represents them
	 * 
	 * @param primeSize
	 *            The max size of the prime in bits
	 * @param powMax
	 *            The max value for the power
	 * @return A new IntegersModPrimePower group
	 */
	public static IntegersModPrimePower generateRandom(int primeSize, int powMax) {
		if (powMax < 1) {
			throw new IllegalArgumentException(
					"powMax must be strictly positive");
		}

		SecureRandom rng = new SecureRandom();

		BigInteger prime = BigInteger.probablePrime(primeSize, rng); // How
																		// convenient!
		int pow = rng.nextInt(powMax);

		return new IntegersModPrimePower(prime, pow);
	}

	public IntegersModPrimePower(BigInteger prime, BigInteger pow) {
		this(prime, pow.intValue());
	}

	public IntegersModPrimePower(BigInteger prime, int pow) {
		super(prime.pow(pow));

		// Fermat's Primality Test
		// The universe is more likely to end before this returns a false
		// positive for PRIME_TEST = 500
		if (!prime.isProbablePrime(IntegersModPrimePower.PRIME_TEST)) {
			throw new IllegalArgumentException(
					"The prime provided is actually composite.");
		}

		this.prime = prime;
		this.pow = pow;
	}

	public BigInteger getPrime() {
		return prime;
	}

	public int getPow() {
		return pow;
	}

	@Override
	protected BigInteger[] getParameters() {
		return new BigInteger[] { prime, BigInteger.valueOf(pow) };
	}
}

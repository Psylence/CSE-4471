package edu.osu.AU13.cse4471.securevote.math;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class MathUtils {
	private static ThreadLocal<Random> RANDOM = new ThreadLocal<Random>() {
		@Override
		protected Random initialValue() {
			return new SecureRandom();
		};
	};

	private MathUtils() {
	}

	public static BigInteger randomBigInteger(BigInteger bound) {
		if (bound.compareTo(BigInteger.ZERO) <= 0) {
			throw new IllegalArgumentException(
					"randomBigInteger: bound must be positive (given bound of "
							+ bound.toString());
		}

		BigInteger cand;
		do {
			cand = new BigInteger(bound.bitCount(), MathUtils.RANDOM.get());
		} while (cand.compareTo(bound) >= 0);

		return cand;
	}

	public static BigInteger randomBigInteger(BigInteger lower, BigInteger upper) {
		return MathUtils.randomBigInteger(upper.subtract(lower)).add(lower);
	}
}

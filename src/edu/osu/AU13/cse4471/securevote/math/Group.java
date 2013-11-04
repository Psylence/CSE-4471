package edu.osu.AU13.cse4471.securevote.math;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Arrays;

import android.util.Log;

/**
 * Represents a finite group. We should have at least one implementation of this
 * interface.
 * 
 * Since certain groups are better than others (these slides <a
 * href="http://www.math.dartmouth.edu/~carlp/dltalk09.pdf">
 * http://www.math.dartmouth.edu/~carlp/dltalk09.pdf</a> suggest that the
 * easiest groups to work with (groups of integers, integers mod <i>m</i>, and
 * the multiplicative groups arising from integers mod <i>m</i> are not as
 * secure as elliptic curve groups.
 * 
 * Of course, I know next to nothing about elliptic curve cryptography. My
 * solution was to abstract away the group operation, so that if we felt the
 * need to drop in elliptic curves later on, we can just implement this
 * interface.
 * 
 * @author andrew
 * 
 */
public abstract class Group {
  /**
   * Returns the identity element in this group.
   * 
   * @return
   */
  public abstract GroupElement identity();

  /**
   * Computes <i>a*b</i> in this group.
   * 
   * @param a
   *          One group element
   * @param b
   *          Another group elements
   * @return <i>a*b</i>
   * @throws IllegalArgumentException
   *           if a or b does not belong to this group
   */
  public abstract GroupElement multiply(GroupElement a, GroupElement b)
      throws IllegalArgumentException;

  public abstract GroupElement inverse(GroupElement g)
      throws IllegalArgumentException;

  /**
   * Computes <i>g<sup>n</sup></i> in this group.
   * 
   * @param g
   *          A group element
   * @param n
   *          Exponent to calculate
   * @return <i>g<sup>n</sup></i>
   */
  public GroupElement exponent(GroupElement g, BigInteger n) {
    if (this != g.getGroup()) {
      throw new IllegalArgumentException(g.toString()
          + " does not belong to group " + toString());
    }

    if (n.signum() == 0) {
      return identity();
    } else if (n.signum() < 0) {
      return exponentImpl(inverse(g), n.negate());
    } else {
      return exponentImpl(g, n);
    }
  }

  /**
   * <p>
   * Actual implementation of the exponential algorithm. Note that
   * {@link #exponent} may be called with <i>n</i> <= 0; but the default
   * implementation of <code>exponent</code> reduces to the positive-exponent
   * case then calles this method. It is guaranteed that when this method is
   * called, <i>n</i> is greater than 0. Subclasses may wish to override this if
   * they have a more efficient exponentiation implementation. However, beware
   * of <a href="http://en.wikipedia.org/wiki/Side_channel_attack"> side-channel
   * attacks</a>!
   * </p>
   * 
   * <p>
   * The default exponentiation uses a variant of exponentiation by squaring,
   * called Montgomery's Ladder. See <a href=
   * "http://en.wikipedia.org/wiki/Exponentiation_by_squaring#Montgomery.27s_ladder_technique"
   * > wikipedia</a> for pseudocode and <a
   * href="http://cr.yp.to/bib/2003/joye-ladder.pdf">this paper</a> for a more
   * rigorous discussion.
   * </p>
   * 
   * @param g
   *          The group element
   * @param n
   *          The (positive) exponent to compute
   * @return <i>g<sup>n</sup></g>
   */
  protected GroupElement exponentImpl(GroupElement g, BigInteger n) {
    GroupElement x1 = g, x2 = multiply(g, g);

    for (int i = n.bitLength() - 2; i >= 0; i--) {
      if (!n.testBit(i)) {
        x2 = multiply(x1, x2);
        x1 = multiply(x1, x1);
      } else {
        x1 = multiply(x1, x2);
        x2 = multiply(x2, x2);
      }
    }

    return x1;
  }

  /**
   * Groups are expected to be parameterized by one or more integer values (e.g.
   * the modulus for groups of integers mod <i>m</i>). This methods returns a
   * parameterization of this particular instance of the group.
   * 
   * The class implementing the group is expected to have a constructor which
   * takes as arguments <i>k</i> BigIntegers, where <i>k</i> is the length of
   * the returned array. If object created by that constructor should be
   * identical to this group.
   * 
   * @return a parameterization of this group
   */
  protected abstract BigInteger[] getParameters();

  /**
   * Returns a name for this group, guaranteed to be of the format:
   * 
   * <pre>
   * [classname;n1,n2,...,nk]
   * </pre>
   * 
   * where <i>classname</i> is the canonical name of the Java class implementing
   * the group, and <i>n1</i>, ..., <i>nk</i> are integer parameters. Note that
   * the semicolon will still be present in the output even if <i>k</i>=0.
   * 
   * @return
   */
  @Override
  public final String toString() {
    BigInteger[] params = getParameters();
    String name = this.getClass().getCanonicalName();

    StringBuilder sb = new StringBuilder(16 * params.length + 32);

    sb.append('[').append(name).append(';');

    for (int i = 0; i < params.length; i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(params[i].toString());
    }

    sb.append(']');

    return sb.toString();
  }

  /**
   * Reconstruct a group from its String representation. This is the reason for
   * the highly-specific {@link #toString} format.
   * 
   * @param s
   *          The string representation of the group to instantiate
   * @return An instantiation of that group
   */
  public static Group fromString(String s) {
    Exception error = null;

    if (s.length() >= 2) {
      // Strip off '[' and ']'
      s = s.substring(1, s.length() - 1);

      // Separate the name of the class and the parameters
      String[] nameAndParams = s.split(";");
      if (nameAndParams.length == 2) {
        String name = nameAndParams[0];
        String paramsString = nameAndParams[1];

        // Parse the parameters into BigIntegers, to pass to the constructor
        String[] paramStringArray = paramsString.split(",");
        Object[] params = new Object[paramStringArray.length];

        for (int i = 0; i < paramStringArray.length; i++) {
          params[i] = new BigInteger(paramStringArray[i]);
        }

        // Try to create the group via reflection.
        // There are many ways this could fail; if it does, just log the error
        // and report the failure via IllegalArgumentException.
        try {
          Class<?> clazz = Class.forName(name);
          Constructor<?> ctor = clazz.getConstructor(Group
              .getArgList(params.length));

          // Make sure the class we're creating is actually a Group
          if (Group.class.isAssignableFrom(clazz)) {
            return (Group) ctor.newInstance(params);
          }
        } catch (ClassNotFoundException e) {
          error = e;
        } catch (NoSuchMethodException e) {
          error = e;
        } catch (IllegalArgumentException e) {
          error = e;
        } catch (InstantiationException e) {
          error = e;
        } catch (IllegalAccessException e) {
          error = e;
        } catch (InvocationTargetException e) {
          error = e;
        }
      }
    }

    if (error != null) {
      Log.d(Group.class.getSimpleName(), "Could not instantiate group '" + s
          + "'.  Reason:\n", error);
    }

    throw new IllegalArgumentException(s
        + " is not a String representation of a group");
  }

  /**
   * Reconstruct a GroupElement belonging to this group from a stirng
   * representation.
   * 
   * @param s
   *          A string representation, as returned by the toString method of an
   *          element belonging to this group
   * @return the group element
   */
  public abstract GroupElement elementFromString(String s);

  /**
   * Helper method used by {@link #fromString} used to reflectively find a
   * constructor for a group.
   * 
   * @param len
   *          The number of parameters needed
   * @return An array of <i>len</i> copies of <code>BigInteger.class</code>,
   *         used to search for an acceptable constructor.
   */
  private static final Class<?>[] getArgList(int len) {
    Class<?>[] ret = new Class<?>[len];
    Arrays.fill(ret, BigInteger.class);
    return ret;
  }

}

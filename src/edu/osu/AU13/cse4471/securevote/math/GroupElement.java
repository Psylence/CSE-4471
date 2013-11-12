package edu.osu.AU13.cse4471.securevote.math;

import java.math.BigInteger;

public abstract class GroupElement {
  /**
   * Retrieves the group to which this element belongs.
   * 
   * @return The group to which this element belongs
   */
  public abstract Group getGroup();
  
  public abstract BigInteger getValue();

  /**
   * Compute <i>this*other</i>
   * 
   * @param other
   *          Another element of the same group
   * @return The product <i>this*other</i>
   */
  public final GroupElement mult(GroupElement other) {
    return getGroup().multiply(this, other);
  }

  /**
   * Compute <i>other*this</i>
   * 
   * @param other
   *          Another element of the group
   * @return The product <i>other*this</i>
   */
  public final GroupElement leftMultiplyBy(GroupElement other) {
    return getGroup().multiply(other, this);
  }

  /**
   * Computes the inverse of <i>this</i>
   * 
   * @return The inverse of <i>this</i>
   */
  public final GroupElement inverse() {
    return getGroup().inverse(this);
  }

  /**
   * Computes <i>this<sup>n</sup></i>
   * 
   * @param n
   *          The exponent to compute
   * @return <i>this<sup>n</sup></i>
   */
  public final GroupElement exp(BigInteger n) {
    return getGroup().exponent(this, n);
  }
  
}

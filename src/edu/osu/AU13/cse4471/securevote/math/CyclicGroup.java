package edu.osu.AU13.cse4471.securevote.math;


public abstract class CyclicGroup extends Group {
  /**
   * Returns a random generator of this group. Note that this should use a
   * secure RNG behind-the-scenes, since we're doing cryptography.
   * 
   * @return A generator of this group
   */
  public abstract GroupElement getRandomGenerator();
}

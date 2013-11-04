package edu.osu.AU13.cse4471.securevote;

import java.util.Locale;

public class Voter extends User {
  /**
   * Create a Voter, assigned the given email address
   * 
   * @param email
   */
  public Voter(String email, Poll poll) {
    super(email, poll);
  }

  /**
   * Serialize this Voter to a String, suitable for saving in a file or
   * database, and later reconstructing via {@link #fromString}..
   * 
   * @return a string representation of this Voter object
   */
  @Override
  public String toString() {
    return String.format(Locale.US, "[Voter:%d,%s]", this.getPoll().getId(),
        this.getEmail());
  }

  /**
   * Reconstruct a Voter from the output of toString.
   * 
   * @param fromString
   *          a string representation of a Voter, as returned by toString
   * @return A reconstructed Voter object
   */
  public static Voter fromString(String s) {
    if (s.startsWith("[Voter:") && s.endsWith("]")) {
      String[] pollAndEmail = s.substring("[Voter:".length(), s.length() - 1)
          .split(",", 2);
      if (pollAndEmail.length == 2) {
        int pollId = -1;
        try {
          pollId = Integer.valueOf(pollAndEmail[0]);
        } catch (NumberFormatException e) {

        }

        String email = pollAndEmail[1];

        if (pollId != -1) {
          return new Voter(email, PollDB.getInstance().getPoll(pollId));
        }
      }
    }
    throw new IllegalArgumentException("'" + s
        + "' is not a valid encoding of Voter");
  }
}

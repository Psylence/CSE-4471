package edu.osu.AU13.cse4471.securevote;

import java.util.Locale;

import edu.osu.AU13.cse4471.securevote.math.GroupElement;

public class Tallier extends User {
  private GroupElement pubKey;

  private static String TOSTRING_PREFIX = "[Tallier:";
  private static String TOSTRING_SUFFIX = "]";

  public Tallier(String email, Poll poll) {
    super(email, poll);
    pubKey = null;
  }

  public Tallier(String email, Poll poll, GroupElement pubKey) {
    super(email, poll);
    this.pubKey = pubKey;
  }

  @Override
  public String toString() {
    // @formatter:off
    // TODO Consider email addresses containing commas.  For now, screw 'em.
    return String.format(Locale.US, "%s%d,%s,%s%s",
        Tallier.TOSTRING_PREFIX,
        this.getPoll().getId(),
        this.getEmail(),
        pubKey == null ? "null" : pubKey.toString(),
        Tallier.TOSTRING_SUFFIX
    );
    // @formatter:on
  }

  /**
   * Reconstruct an instance of Tallier from a string encoding, as returned by
   * {@link #toString}.
   * 
   * @param s
   *          string as returned by toString
   * @return instance of Tallier
   */
  public static Tallier fromString(String s) {
    if (s.startsWith(Tallier.TOSTRING_PREFIX)
        && s.endsWith(Tallier.TOSTRING_SUFFIX)) {
      String noPrefix = s.substring(Tallier.TOSTRING_PREFIX.length(),
          s.length() - Tallier.TOSTRING_SUFFIX.length());
      String[] parts = noPrefix.split(",", 3);
      if (parts.length == 3) {
        int pollId = -1;
        try {
          pollId = Integer.valueOf(parts[0]);
        } catch (NumberFormatException e) {
        }
        Poll poll = PollDB.getInstance().getPoll(pollId);

        if (poll != null) {
          String email = parts[1];
          String pubkeyString = parts[2];
          GroupElement pubkey = pubkeyString.equals("null") ? null : poll
              .getGroup().elementFromString(pubkeyString);

          return new Tallier(email, PollDB.getInstance().getPoll(pollId),
              pubkey);
        }
      }
    }

    throw new IllegalArgumentException("'" + s
        + "' is not a valid encoding of Tallier");
  }
}

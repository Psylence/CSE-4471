package edu.osu.AU13.cse4471.securevote;


/**
 * This class represents a poll known to a single client. Its methods will
 * handle encryption and determining the result for a poll. It is assumed that
 * emailing the contents will be the responsibility of another class. It is just
 * a basic interface right now, it will be fleshed out when we have
 * implementation details.
 */
public class Poll {
  private String title;
  private int id;

  private long pollKey;
  private long personalKey;

  private long[] votes;
  private int totalParticipants;

  /**
   * Create a new poll object from an email.
   * 
   * @param emailContent
   *          The content of the email to parse
   */
  public Poll(String emailContent) {
    // TODO
  }

  /**
   * Create a new poll object loaded from the database.
   */
  // TODO add rest of attributes to this ctor
  public Poll(int id, String title) {
    this.id = id;
    this.title = title;
  }

  /**
   * 
   */

  /**
   * Get the unique ID assigned to this poll. The poll can be referenced using
   * this ID.
   */
  public int getId() {
    return id;
  }

  /**
   * Encrypt your choice using your personal key.
   * 
   * @param choice
   *          Your choice
   * @return Your choice encrypted
   */
  public long generateVote(int choice) {
    // TODO
    return -1;
  }

  public int countVotes() {
    // TODO
    return -1;
  }

  @Override
  public String toString() {
    return title + ":" + totalParticipants + ":" + pollKey + ":" + personalKey;
  }

}

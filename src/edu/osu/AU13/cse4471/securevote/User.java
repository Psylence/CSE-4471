package edu.osu.AU13.cse4471.securevote;

/**
 * Common base class for users and talliers.
 * 
 * Encapsulates their identfying information (ie, email address) and handles
 * sending communications to them.
 * 
 * @author andrew
 * 
 */
public abstract class User {
  /**
   * User's email address
   */
  private String email;

  /**
   * Each user's data is only stored in the context of a given Poll (the poll in
   * which he is a voter or a tallier. If the same user participates in multiple
   * polls, we'll allow ourselves to keep multiple records of him, so that we
   * can easily store Poll-specific data (like public keys, votes, etc) on the
   * User object. Furthermore, if the same person (same email address) is
   * participating as both a Voter and a Tallier, we'll keep two objects (
   * {@link Voter} and {@link Tallier}).
   */
  private Poll poll;

  /**
   * Initialize the fields in User
   * 
   * @param email
   *          user's email address
   */
  protected User(String email, Poll poll) {
    this.email = email;
    this.poll = poll;
  }

  /**
   * Retrieve the user's email address
   * 
   * @return the user's email address
   */
  public String getEmail() {
    return email;
  }

  /**
   * Retrieve the poll that this User object is participating in.
   * 
   * @return the poll in which this User is participating.
   */
  public Poll getPoll() {
    return poll;
  }

  /**
   * Send an email to this user
   * 
   * @param args
   *          Don't know yet
   */
  public void sendEmail(Void... args) {
    return;
  }
}

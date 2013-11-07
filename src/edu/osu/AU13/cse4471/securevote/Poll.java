package edu.osu.AU13.cse4471.securevote;

import java.util.Collections;
import java.util.List;

import edu.osu.AU13.cse4471.securevote.math.Group;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;

/**
 * This class represents a poll known to a single client.
 */
public class Poll {
  /**
   * Private ID, used internally to identify this Poll, especially when
   * recreating it from a file/DB
   */
  private int id;

  /**
   * Title to identify this poll to users
   */
  private String title;

  /**
   * Description of the poll shown to users
   */
  private String desc;

  /**
   * List of voters in this poll. These are the users who can actually cast
   * votes.
   */
  private List<Voter> voters;

  /**
   * List of talliers in this poll. These are the users to whom results are sent
   * to be decoded.
   */
  private List<Tallier> talliers;

  /**
   * Group used for encryption in this poll
   */
  private Group group;

  /**
   * Parameters <i>g</i> and <i>G</i> are two group generators, used in
   * encryption
   */
  private GroupElement g, G;

  /**
   * Create a new Poll object
   * 
   * @param id
   *          unique identifier
   * @param title
   *          Display title
   * @param desc
   *          Display description
   * @param voters
   *          List of voters
   * @param talliers
   *          List of talliers
   * @param group
   *          Group to use for cryptography
   * @param g
   *          Group generator
   * @param G
   *          Another group generator
   */
  public Poll(int id, String title, String desc, List<Voter> voters,
      List<Tallier> talliers, Group group, GroupElement g, GroupElement G) {
    if (id == -1) {
      throw new IllegalArgumentException("Poll cannot have an ID of -1");
    }

    if (title == null || desc == null || voters == null || talliers == null
        || group == null || g == null || G == null) {
      throw new NullPointerException();
    }

    if (!g.getGroup().equals(group) || !G.getGroup().equals(group)) {
      throw new IllegalArgumentException(
          "Poll parameters g and G must both belong to group");
    }

    if (voters.isEmpty() || talliers.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot create a poll with no voters or with no talliers");
    }

    this.id = id;
    this.title = title;
    this.desc = desc;
    this.voters = Collections.unmodifiableList(voters);
    this.talliers = Collections.unmodifiableList(talliers);
    this.group = group;
    this.g = g;
    this.g = G;
  }

  /**
   * Get the unique ID assigned to this poll. The poll can be referenced using
   * this ID. See {@link PollDB}.
   * 
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the title of this poll, used to identify the poll to users.
   * 
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets the description of this poll, used to describe the poll to users.
   * 
   * @return a description of the poll
   */
  public String getDesc() {
    return desc;
  }

  /**
   * Returns the group used for cryptography in this poll
   * 
   * @return A Group
   */
  public Group getGroup() {
    return group;
  }

  /**
   * Return one of the generators of the group (see {@link #getGroup()}). This
   * is the generator denoted by <i>g</i>.
   * 
   * @return <i>g</i>
   */
  public GroupElement getg() {
    return g;
  }

  /**
   * Return the other generator of the group (see {@link #getGroup()}. This is
   * the generator denoted by <i>G</i>.
   * 
   * @return <i>G</i>
   */
  public GroupElement getG() {
    return G;
  }

  /**
   * Returns an (unmodifiable) list of voters in this poll.
   * 
   * @return list of voters
   */
  public List<Voter> getVoters() {
    return voters;
  }

  public List<Tallier> getTalliers() {
    return talliers;
  }
}

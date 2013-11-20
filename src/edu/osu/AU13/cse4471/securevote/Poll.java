package edu.osu.AU13.cse4471.securevote;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;
import edu.osu.AU13.cse4471.securevote.math.Group;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;
import edu.osu.AU13.cse4471.securevote.math.IntegersModPrimePower;

/**
 * This class represents a poll known to a single client.
 */
public class Poll implements JSONSerializable {
  private static final int SANITY_BOUND = 100;

  private static final String JSON_ID = "id";
  private static final String JSON_TITLE = "title";
  private static final String JSON_DESC = "desc";
  private static final String JSON_VOTERS = "voters";
  private static final String JSON_TALLIERS = "talliers";
  private static final String JSON_GROUP = "group";
  private static final String JSON_LITTLE_G = "g1";
  private static final String JSON_BIG_G = "g2";

  /**
   * Private ID, used internally to identify this Poll, especially when
   * recreating it from a file/DB
   */
  private UUID id;

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
   * Is the current user a tallier in this poll?
   */
  private boolean isTallier;

  /**
   * Save the current user's secret key, if the current user is a tallier.
   * Encrypt this when saving to disk!
   */
  private PrivateKey tallierKey;

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
   */
  public Poll(UUID id, String title, String desc) {
    if (id == null || title == null || desc == null) {
      throw new NullPointerException();
    }

    this.id = id;
    this.title = title;
    this.desc = desc;
    this.voters = null;
    this.talliers = null;
    this.isTallier = false;
    this.tallierKey = null;

    int outerSanity = 0;
    int sanity;
    do {
      IntegersModPrimePower gp = IntegersModPrimePower.generateRandom();
      this.group = gp;

      // Create generators
      this.g = gp.getRandomGenerator();
      sanity = 0;
      do {
        this.G = gp.getRandomGenerator();
        sanity++;
      } while (g.equals(G) && sanity < Poll.SANITY_BOUND);
      outerSanity++;
    } while (sanity == Poll.SANITY_BOUND && outerSanity < Poll.SANITY_BOUND);
    if (outerSanity == Poll.SANITY_BOUND) {
      throw new RuntimeException(
          "You're insane: failed to create a group with two random generators.");
    }
  }

  /**
   * Deserialize a Poll from a JSONObject.
   * 
   * @param obj
   *          JSONObject as returned by {@link toJson()}.
   * @throws IllegalArgumentException
   */
  public Poll(JSONObject obj) throws IllegalArgumentException {
    try {
      id = UUID.fromString(obj.getString(Poll.JSON_ID));
      title = obj.getString(Poll.JSON_TITLE);
      desc = obj.getString(Poll.JSON_DESC);

      // Must deserialize encryption parameters before deserializing
      // talliers!
      group = Group.fromString(obj.getString(Poll.JSON_GROUP));
      g = group.elementFromString(obj.getString(Poll.JSON_LITTLE_G));
      G = group.elementFromString(obj.getString(Poll.JSON_BIG_G));

      voters = JSONUtils.fromArray(obj.getJSONArray(Poll.JSON_VOTERS),
          new Voter.VoterDeserializer(this));
      talliers = JSONUtils.fromArray(obj.getJSONArray(Poll.JSON_TALLIERS),
          new Tallier.TallierDeserializer(this));

      isTallier = false;
      tallierKey = null;
    } catch (JSONException e) {
      throw new IllegalArgumentException("JSON object does not encode a Poll",
          e);
    }
  }

  /**
   * Get the unique ID assigned to this poll. The poll can be referenced using
   * this ID. See {@link PollDB}.
   * 
   * @return the id
   */
  public UUID getId() {
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

  /**
   * Saves a new list of voters for this poll. This will fail if the poll
   * already has voters assigned to it. It is intended only for use with the
   * constructor that does not specify voters (see {@link Poll(UUID, String,
   * String, Group, GroupElement, GroupElement)})
   * 
   * @param voters
   */
  public void setVoters(List<Voter> voters) {
    if (this.voters != null) {
      throw new IllegalStateException("Poll already has voters set");
    }
    this.voters = Collections.unmodifiableList(voters);
  }

  /**
   * Saves a new list of talliers for this poll. This will fail if the poll
   * already has talliers assigned to it. It is intended only for use with the
   * constructor that does not specify talliers (see {@link Poll(UUID, String,
   * String, Group, GroupElement, GroupElement)})
   * 
   * @param voters
   */
  public void setTalliers(List<Tallier> talliers) {
    if (this.talliers != null) {
      throw new IllegalStateException("Poll already has talliers set");
    }
    this.talliers = Collections.unmodifiableList(talliers);
  }

  public void setTallierSecretKey(PrivateKey key) {
    this.isTallier = true;
    this.tallierKey = key;
  }

  @Override
  public JSONObject toJson() throws JSONException {
    JSONObject obj = new JSONObject();

    obj.put(Poll.JSON_ID, id.toString());
    obj.put(Poll.JSON_TITLE, title);
    obj.put(Poll.JSON_DESC, desc);
    obj.put(Poll.JSON_VOTERS, JSONUtils.toArray(voters));
    obj.put(Poll.JSON_TALLIERS, JSONUtils.toArray(talliers));
    obj.put(Poll.JSON_GROUP, group.toString());
    obj.put(Poll.JSON_LITTLE_G, g.toString());
    obj.put(Poll.JSON_BIG_G, G.toString());

    return obj;
  }
}

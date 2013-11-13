package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;
import edu.osu.AU13.cse4471.securevote.math.CyclicGroup;
import edu.osu.AU13.cse4471.securevote.math.Group;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;
import edu.osu.AU13.cse4471.securevote.math.IntegersModM;
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
	 * List of talliers in this poll. These are the users to whom results are
	 * sent to be decoded.
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
	 *            unique identifier
	 * @param title
	 *            Display title
	 * @param desc
	 *            Display description
	 * @param voters
	 *            List of voters
	 * @param talliers
	 *            List of talliers
	 */
	public Poll(UUID id, String title, String desc, List<Voter> voters,
			List<Tallier> talliers) {
		if (id == null || title == null || desc == null || voters == null
				|| talliers == null) {
			throw new NullPointerException();
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
			} while (g.equals(G) && sanity < SANITY_BOUND);
			outerSanity++;
		} while (sanity == SANITY_BOUND && outerSanity < SANITY_BOUND);
		if(outerSanity == SANITY_BOUND) throw new RuntimeException("You're insane: failed to create a group with two random generators.");
	}

	/**
	 * Deserialize a Poll from a JSONObject.
	 * 
	 * @param obj
	 *            JSONObject as returned by {@link toJson()}.
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
			talliers = JSONUtils.fromArray(
					obj.getJSONArray(Poll.JSON_TALLIERS),
					new Tallier.TallierDeserializer(this));
		} catch (JSONException e) {
			throw new IllegalArgumentException(
					"JSON object does not encode a Poll", e);
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

package edu.osu.AU13.cse4471.securevote;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;

public class Tallier extends User implements JSONSerializable {
	private static final String JSON_EMAIL = "email";
	private static final String JSON_PRIVKEY = "privkey";
	public static final String JSON_HASSENTPOLL = "hasSentKey";

	private PrivateKey privKey;
	// A map is used in the case of needing to ignore a vote because they failed
	// to send it to every Tallier
	private Map<String, EncryptedPoint> votes;
	private Set<SecretPoint> points;
	private int tallierNum;
	private boolean hasSentKey;

	/**
	 * Create a Tallier with the given email, belonging to the given Poll
	 * 
	 * @param email
	 * @param poll
	 */
	public Tallier(String email, Poll poll) {
		this(email, poll, new PrivateKey(poll.getGroup(), poll.getg()), false);
	}

	/**
	 * Create a Tallier with the given email, belonging to the given poll, with
	 * the given public key
	 * 
	 * @param email
	 * @param poll
	 * @param privKey
	 * @param hasSentKey
	 */
	public Tallier(String email, Poll poll, PrivateKey privKey,
			boolean hasSentKey) {
		super(email, poll);
		this.privKey = privKey;
		this.votes = new TreeMap<String, EncryptedPoint>();
		this.points = new TreeSet<SecretPoint>();
		this.tallierNum = poll.getTalliers().indexOf(email);
		this.hasSentKey = hasSentKey;
	}

	public void sendPublicKey(Activity caller) {
		Poll p = this.getPoll();

		// Get list of people to send public key to
		String[] recipients = (String[]) p.getVoters().toArray();

		// Construct and send the email
		String title = "Securevote: Public Key From Tallier " + tallierNum;
		String attach;
		try {
			attach = privKey.getPublicKey().toJson().toString();
		} catch (JSONException e) {
			Context context = caller.getApplicationContext();
			CharSequence text = "Failed to encode key as a JSON object.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
			return;
		}

		Email email = new Email(title, caller.getResources().getString(
				R.string.email_body), attach);
		Emailer.sendEmail(email, recipients, caller, getPoll());
	}

	public void receiveVote(Activity caller, EncryptedPoint vote, String email) {
		// Ensure the point is meant for you
		if (vote.getX() != tallierNum + 1) {
			Context context = caller.getApplicationContext();
			CharSequence text = "Vote isn't encrypted for this tallier.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
		}
		// Check to see if this vote has been cast already
		else if (votes.containsKey(email)) {
			Context context = caller.getApplicationContext();
			CharSequence text = "Vote has already been cast for this person.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
		}
		// Store the vote
		else {
			votes.put(email, vote);
		}

	}

	public void sendResult(Activity caller) {
		// Calculate the result
		int total = 0;
		for (EncryptedPoint point : votes.values()) {
			GroupElement decrypted = privKey.decode(point.getY());
			if (decrypted.getValue().bitLength() >= 32) {
				total += decrypted.getValue().intValue();
			}
		}

		SecretPoint result = new SecretPoint(total, tallierNum);

		// Send the result out to all of the voters
		Poll p = this.getPoll();

		// Get list of people to send public key to
		String[] recipients = (String[]) p.getVoters().toArray();

		// Construct and send the email
		String title = "Securevote: Result From Tallier " + tallierNum;
		String attach;
		try {
			attach = result.toJson().toString();
		} catch (JSONException e) {
			Context context = caller.getApplicationContext();
			CharSequence text = "Failed to encode result as a JSON object.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
			return;
		}

		Email email = new Email(title, caller.getResources().getString(
				R.string.email_body), attach);
		Emailer.sendEmail(email, recipients, caller, getPoll());
	}

	/**
	 * Converts this object into a JSON representation
	 */
	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();

		obj.put(Tallier.JSON_EMAIL, getEmail());
		obj.put(Tallier.JSON_PRIVKEY, privKey.getPublicKey().toString());
		obj.put(Tallier.JSON_HASSENTPOLL, hasSentKey);

		return obj;
	}

	/**
	 * Class used to convert JSON back into a tallier. Note: the Poll supplied
	 * in the constructor must (at least) have a valid group accessible via
	 * {@link Poll#getGroup()}.
	 * 
	 * @author andrew
	 * 
	 */
	public static class Deserializer implements JSONDeserializer<Tallier> {
		private Poll mPoll;

		/**
		 * Create a new deserializer, which associates Talliers with the given
		 * poll. The Poll must (at least) have a valid group accessible via
		 * {@link Poll#getGroup()}.
		 * 
		 * @param p
		 */
		public Deserializer(Poll p) {
			mPoll = p;
		}

		@Override
		public Tallier fromJson(JSONObject obj) throws JSONException {
			// Get the email address of this Tallier object
			String email = obj.getString(Tallier.JSON_EMAIL);

			// Deserialize the private key
			PrivateKey privKey;
			privKey = new PrivateKey.Deserializer().fromJson(obj
					.getJSONObject(Tallier.JSON_PRIVKEY));
			boolean hasSentKey = obj.getBoolean(Tallier.JSON_HASSENTPOLL);

			return new Tallier(email, mPoll, privKey, hasSentKey);
		}
	}

	public boolean hasAllVotes() {
		return votes.keySet().containsAll(getPoll().getVoters());
	}

	public boolean hasResults() {
		return points.size() == getPoll().getVoters().size();
	}

}

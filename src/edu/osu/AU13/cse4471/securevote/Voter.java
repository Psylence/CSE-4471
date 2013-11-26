package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;

public class Voter extends User implements JSONSerializable {
	private static final String JSON_EMAIL = "email";
	private static final String JSON_KEYS = "keys";
	private static final String JSON_CHOICE = "choice";
	public static final String JSON_HIDDEN_VOTE = "hidden_vote";

	private PublicKey[] keys;
	private SecretPolynomial poly;
	private GroupElement hiddenVote;

	/**
	 * Create a Voter, assigned the given email address
	 * 
	 * @param email
	 */
	public Voter(String email, Poll poll) {
		super(email, poll);

		int numTalliers = poll.getTalliers().size();
		keys = new PublicKey[numTalliers];
		for (int i = 0; i < numTalliers; i++) {
			keys[i] = null;
		}
		poly = null;
	}

	public Voter(String email, Poll poll, PublicKey[] keys,
			SecretPolynomial poly, GroupElement hiddenVote) {
		super(email, poll);
		this.keys = keys;
		this.poly = poly;
		this.hiddenVote = hiddenVote;
	}

	public void receiveKey(Context con, String email, PublicKey key) {
		// Find which tallier this key is from
		List<String> talliers = getPoll().getTalliers();
		int index = talliers.indexOf(email);

		// Ensure that this was sent from one of the talliers
		if (index == -1) {
			Context context = con.getApplicationContext();
			CharSequence text = "This key is from an invalid email address.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
		}
		// Throw an alert if the key had already been assigned
		else if (keys[index] != null) {
			Context context = con.getApplicationContext();
			CharSequence text = "This key was already assigned.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
		}
		// Otherwise, assign the key
		else {
			keys[index] = key;
			DiskPersister dp = DiskPersister.getInst();
			dp.save(getPoll(), this, dp.loadTallier(getPoll().getId(), con),
					con);
		}
	}

	public boolean isReadyToVote() {
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == null) {
				return false;
			}
		}
		return true;
	}

	public void vote(Activity caller) {
		if (!hasVoted()) {
			Context context = caller.getApplicationContext();
			CharSequence text = "You must make a choice before a vote can be sent.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
			return;
		}

		// Create an email that can be sent to every tallier
		List<String> talliers = getPoll().getTalliers();

		String subject = "Securevoting: Encrypted Vote";
		JSONArray arr = new JSONArray();
		JSONObject obj = new JSONObject();
		try {
			for (int i = 0; i < talliers.size(); i++) {
				EncryptedPoint p = new EncryptedPoint(poly.getPoint(i), keys[i]);
				arr.put(p.toJson());
			}
			obj.put(Constants.JSON_PHASE, Constants.PHASE_VOTE);
			obj.put(Constants.JSON_ENCR_POINTS, arr);
			obj.put(Constants.JSON_POLL_ID, getPoll().getId());
			obj.put(Constants.JSON_VOTE_FROM, getEmail());
		} catch (JSONException e) {
			Context context = caller.getApplicationContext();
			CharSequence text = "Failed to encode point as JSON object.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
			return;
		}

		Email email = new Email(subject, caller.getResources().getString(
				R.string.email_body), obj.toString());
		Emailer.sendEmail(email, talliers.toArray(new String[0]), caller,
				getPoll());
	}

	public void vote(Activity caller, boolean selection) {
		if (!isReadyToVote()) {
			Context context = caller.getApplicationContext();
			CharSequence text = "You need a key from every tallier to vote.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
			return;
		}

		if (poly == null) {
			poly = new SecretPolynomial(getPoll().getTalliers().size(),
					getPoll().getGroup().order());
			BigInteger vote = poly.getSecret().add(
					selection ? BigInteger.ONE : BigInteger.ZERO);
			hiddenVote = getPoll().getg().exp(vote);

			// Encode the choice
			poly = new SecretPolynomial(getPoll().getTalliers().size());
		}

		vote(caller);
	}

	/**
	 * Test whether I've already voted
	 * 
	 * @return
	 */
	public boolean hasVoted() {
		return poly != null;
	}

	/**
	 * If I've already voted, return my choice. Otherwise, return null
	 * 
	 * @return
	 */
	public GroupElement getHiddenVote() {
		return hiddenVote;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();

		obj.put(Voter.JSON_EMAIL, getEmail());

		JSONArray arr = new JSONArray();
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == null) {
				arr.put(JSONObject.NULL);
			} else {
				arr.put(keys[i].toJson());
			}
		}
		obj.put(Voter.JSON_KEYS, arr);

		if (poly == null) {
			obj.put(Voter.JSON_CHOICE, JSONObject.NULL);
		} else {
			obj.put(Voter.JSON_CHOICE, poly.toJson());
		}

		if (hiddenVote == null) {
			obj.put(Voter.JSON_HIDDEN_VOTE, JSONObject.NULL);
		} else {
			obj.put(Voter.JSON_HIDDEN_VOTE, hiddenVote.toString());
		}

		return obj;
	}

	public static class Deserializer implements JSONDeserializer<Voter> {
		private final Poll mPoll;

		public Deserializer(Poll p) {
			mPoll = p;
		}

		@Override
		public Voter fromJson(JSONObject obj) throws JSONException,
				IllegalArgumentException {
			String email = obj.getString(Voter.JSON_EMAIL);

			JSONArray arr = obj.getJSONArray(Voter.JSON_KEYS);
			PublicKey[] keys = new PublicKey[arr.length()];
			PublicKey.Deserializer d = new PublicKey.Deserializer(
					mPoll.getGroup());
			for (int i = 0; i < keys.length; i++) {
				Object key = arr.get(i);
				if (key instanceof JSONObject) {
					keys[i] = d.fromJson((JSONObject) key);
				} else {
					keys[i] = null;
				}
			}

			JSONObject polyObj = obj.getJSONObject(Voter.JSON_CHOICE);
			SecretPolynomial poly;
			if (polyObj.equals(JSONObject.NULL)) {
				poly = null;
			} else {
				poly = new SecretPolynomial.Deserializer().fromJson(polyObj);
			}

			GroupElement hiddenVote;
			Object o = obj.get(Voter.JSON_HIDDEN_VOTE);
			if (o instanceof String) {
				hiddenVote = mPoll.getGroup().elementFromString((String) o);
			} else {
				hiddenVote = null;
			}

			return new Voter(email, mPoll, keys, poly, hiddenVote);
		}
	}
}

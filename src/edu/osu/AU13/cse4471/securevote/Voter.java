package edu.osu.AU13.cse4471.securevote;

import java.math.BigInteger;
import java.util.List;

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

	private PublicKey[] keys;

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
	}

	public void receiveKey(Activity caller, String email, PublicKey key) {
		// Find which tallier this key is from
		List<String> talliers = getPoll().getTalliers();
		int index = talliers.indexOf(email);

		// Ensure that this was sent from one of the talliers
		if (index == -1) {
			Context context = caller.getApplicationContext();
			CharSequence text = "This key is from an invalid email address.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
		}
		// Throw an alert if the key had already been assigned
		else if (keys[index] != null) {
			Context context = caller.getApplicationContext();
			CharSequence text = "This key was already assigned.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
		}
		// Otherwise, assign the key
		else {
			keys[index] = key;
		}
	}

	public boolean isReadyToVote() {
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == null)
				return false;
		}
		return true;
	}

	public void vote(Activity caller, boolean choice) {
		if (!isReadyToVote()) {
			Context context = caller.getApplicationContext();
			CharSequence text = "You need a key from every tallier to vote.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
			return;
		}

		// Encode the choice
		SecretPolynomial poly = new SecretPolynomial(getPoll().getTalliers()
				.size(), choice ? 1 : 0);

		// Create an email that can be sent to every tallier
		List<String> talliers = getPoll().getTalliers();

		String subject = "Securevoting: Encrypted Vote";
		String body = "{";
		for (int i = 0; i < talliers.size(); i++) {
			EncryptedPoint p = new EncryptedPoint(poly.getPoint(i), keys[i]);
			//body += do something here that builds up the body
		}
		
		Email email = new Email(subject, body);
		Emailer.sendEmail(email, (String[])talliers.toArray(), caller);
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();

		obj.put(Voter.JSON_EMAIL, getEmail());

		return obj;
	}

	public static class VoterDeserializer implements JSONDeserializer<Voter> {
		private final Poll mPoll;

		public VoterDeserializer(Poll p) {
			mPoll = p;
		}

		@Override
		public Voter fromJson(JSONObject obj) throws JSONException,
				IllegalArgumentException {
			String email = obj.getString(Voter.JSON_EMAIL);

			return new Voter(email, mPoll);
		}
	}
}

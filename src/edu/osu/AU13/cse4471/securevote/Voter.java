package edu.osu.AU13.cse4471.securevote;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONSerializable;

public class Voter extends User implements JSONSerializable {
	private static final String JSON_EMAIL = "email";
	private static final String JSON_KEYS = "keys";
	private static final String JSON_CHOICE = "choice";

	private PublicKey[] keys;
	private int choice;

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
		choice = -1;
	}
	
	public Voter(String email, Poll poll, PublicKey[] keys, int choice) {
		super(email, poll);
		this.keys = keys;
		this.choice = choice;
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
			if (keys[i] == null) {
				return false;
			}
		}
		return true;
	}

	public void vote(Activity caller, boolean selection) {
		if (!isReadyToVote()) {
			Context context = caller.getApplicationContext();
			CharSequence text = "You need a key from every tallier to vote.";
			int duration = Toast.LENGTH_SHORT;
			Toast.makeText(context, text, duration).show();
			return;
		}

		if(choice == -1) {
			choice = selection ? 1 : 0;
		}
		
		// Encode the choice
		SecretPolynomial poly = new SecretPolynomial(getPoll().getTalliers()
				.size(), choice);

		// Create an email that can be sent to every tallier
		List<String> talliers = getPoll().getTalliers();

		String subject = "Securevoting: Encrypted Vote";
		JSONArray arr = new JSONArray();
		
		for (int i = 0; i < talliers.size(); i++) {
			EncryptedPoint p = new EncryptedPoint(poly.getPoint(i), keys[i]);
			try {
				arr.put(p.toJson());
			} catch (JSONException e) {
				Context context = caller.getApplicationContext();
				CharSequence text = "Failed to encode point as JSON object.";
				int duration = Toast.LENGTH_SHORT;
				Toast.makeText(context, text, duration).show();
				return;
			}
		}

		Email email = new Email(subject, caller.getResources().getString(R.string.email_body), arr.toString());
		Emailer.sendEmail(email, (String[]) talliers.toArray(), caller, getPoll());
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();

		obj.put(Voter.JSON_EMAIL, getEmail());
		
		JSONArray arr = new JSONArray();
		for(int i = 0; i < keys.length; i++) {
			if(keys[i] == null) {
				arr.put(JSONObject.NULL);
			}
			else {
				arr.put(keys[i].toJson());
			}
		}
		obj.put(JSON_KEYS, arr);
		
		obj.put(JSON_CHOICE, choice);

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
			
			JSONArray arr = obj.getJSONArray(JSON_KEYS);
			PublicKey[] keys = new PublicKey[arr.length()];
			PublicKey.Deserializer d = new PublicKey.Deserializer(mPoll.getGroup());
			for(int i = 0; i < keys.length; i++) {
				JSONObject key = (JSONObject) arr.get(i);
				if(key.equals(null)) {
					keys[i] = null;
				}
				else {
					keys[i] = d.fromJson(key);
				}
			}
			
			int choice = obj.getInt(JSON_CHOICE);

			return new Voter(email, mPoll, keys, choice);
		}
	}
}

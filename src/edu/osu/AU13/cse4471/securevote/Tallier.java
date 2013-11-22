package edu.osu.AU13.cse4471.securevote;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	private PrivateKey privKey;
	// A map is used in the case of needing to ignore a vote because they failed to send it to every Tallier
	private Map<String, EncryptedPoint> votes;
	private int tallierNum;

	/**
	 * Create a Tallier with the given email, belonging to the given Poll
	 * 
	 * @param email
	 * @param poll
	 */
	public Tallier(String email, Poll poll, int tallierNum) {
		super(email, poll);

		this.tallierNum = tallierNum;
		votes = new TreeMap<String, EncryptedPoint>();
		privKey = new PrivateKey(poll.getGroup(), poll.getg());
	}

	/**
	 * Create a Taillier with the given email, belonging to the given poll, with
	 * the given public key
	 * 
	 * @param email
	 * @param poll
	 * @param pubKey
	 */
	public Tallier(String email, Poll poll, PrivateKey privKey) {
		super(email, poll);
		this.privKey = privKey;
	}

	public void sendPublicKey(Activity caller) {
		Poll p = this.getPoll();

		// Get list of people to send public key to
		String[] recipients = (String[])p.getVoters().toArray();

		// Construct and send the email
		// TODO do something with the public key to get intents to recognize it
		String title = "Securevote: Public Key From Tallier " + tallierNum;
		String body = privKey.getPublicKey().toString() + " : " + tallierNum;

		Email email = new Email(title, body);
		Emailer.sendEmail(email, recipients, caller);
	}

	public void receiveVote(Activity caller, EncryptedPoint vote, String email) {
		// Ensure the point is meant for you
		if (vote.getX() != tallierNum) {
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
		// TODO change points to be BigIntegers
		int total = 0;
		for(EncryptedPoint point : votes.values()) {
			GroupElement decrypted = privKey.decode(point.getY());
			if(decrypted.getValue().bitLength() >= 32)
			total += decrypted.getValue().intValue();
		}
		
		SecretPoint result = new SecretPoint(total, tallierNum);
		
		// Send the result out to all of the voters
		Poll p = this.getPoll();

		// Get list of people to send public key to
		String[] recipients = (String[])p.getVoters().toArray();

		// Construct and send the email
		// TODO do something with the result to get intents to recognize it
		String title = "Securevote: Public Key From Tallier " + tallierNum;
		String body = result.toString();

		Email email = new Email(title, body);
		Emailer.sendEmail(email, recipients, caller);
	}

	/**
	 * Converts this object into a JSON representation
	 */
	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject obj = new JSONObject();

		obj.put(Tallier.JSON_EMAIL, getEmail());
		if (privKey != null) {
			obj.put(Tallier.JSON_PRIVKEY, privKey.getPublicKey().toString());
		}

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
	public static class TallierDeserializer implements
			JSONDeserializer<Tallier> {
		private Poll mPoll;

		/**
		 * Create a new deserializer, which associates Talliers with the given
		 * poll. The Poll must (at least) have a valid group accessible via
		 * {@link Poll#getGroup()}.
		 * 
		 * @param p
		 */
		public TallierDeserializer(Poll p) {
			mPoll = p;
		}

		@Override
		public Tallier fromJson(JSONObject obj) throws JSONException {
			// Get the email address of this Tallier object
			String email = obj.getString(Tallier.JSON_EMAIL);

			// Deserialize the private key
			PrivateKey privKey;
			String privKeyStr = obj.getString(Tallier.JSON_PRIVKEY);
			privKey = PrivateKey.fromString(mPoll.getGroup(), privKeyStr);

			return new Tallier(email, mPoll, privKey);
		}
	}

}

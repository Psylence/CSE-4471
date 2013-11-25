package edu.osu.AU13.cse4471.securevote;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import edu.osu.AU13.cse4471.securevote.ui.CreatePoll;

public class ProtocolHandler {
	public static void handle(JSONObject json) {
		try {
			String phase = json.getString(Constants.JSON_PHASE);
			if (Constants.PHASE_NEWPOLL.equals(phase)) {
				ProtocolHandler.newPoll(json);
			}
		} catch (JSONException e) {
			Log.e(ProtocolHandler.class.getSimpleName(), "JSON parse error", e);
		}
	}

	private static void newPoll(JSONObject json) throws JSONException {

	}

	public static void sendPoll(Poll p, Activity act) {
		String subject = act.getResources().getString(
				R.string.msg_newpoll_subject);
		String body = String.format(Locale.US,
				act.getResources().getString(R.string.msg_newpoll_body),
				p.getTitle(), p.getDesc());
		String attachment;
		try {
			JSONObject obj = new JSONObject();
			obj.put(Constants.JSON_PHASE, Constants.PHASE_NEWPOLL);
			obj.put(Constants.JSON_POLL, p.toJson());
			attachment = obj.toString();
		} catch (JSONException e) {
			Log.e(CreatePoll.class.getSimpleName(), "Error serializing poll", e);
			return;
		}

		Email email = new Email(subject, body, attachment);

		Set<String> recipSet = new HashSet<String>(p.getVoters());
		recipSet.addAll(p.getTalliers());
		String[] recipients = recipSet.toArray(new String[recipSet.size()]);

		Emailer.sendEmail(email, recipients, act, p);
	}

}

package edu.osu.AU13.cse4471.securevote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.osu.AU13.cse4471.securevote.ui.CreatePoll;

public class ProtocolHandler {
	public static void handle(JSONObject json, Context con) {
		try {
			String phase = json.getString(Constants.JSON_PHASE);
			if (Constants.PHASE_NEWPOLL.equals(phase)) {
				ProtocolHandler.newPoll(json, con);
			}
		} catch (JSONException e) {
			Log.e(ProtocolHandler.class.getSimpleName(), "JSON parse error", e);
		}
	}

	private static void newPoll(JSONObject json, Context con)
			throws JSONException {
		Poll poll = new Poll(json.getJSONObject(Constants.JSON_POLL));
		ProtocolHandler.chooseEmail(poll, con);
	}

	private static void chooseEmail(final Poll poll, final Context con) {
		Set<String> emailSet = new HashSet<String>(poll.getVoters());
		emailSet.addAll(poll.getTalliers());

		final List<String> emails = new ArrayList<String>(emailSet);

		Collections.shuffle(emails);

		AlertDialog.Builder b = new AlertDialog.Builder(con);
		b.setTitle("Choose your email");
		b.setMessage("Choose carefully!");

		ListView lv = new ListView(con);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(con,
				android.R.layout.simple_list_item_1);
		for (String s : emails) {
			adapter.add(s);
		}

		lv.setAdapter(adapter);
		b.setView(lv);

		final AlertDialog dialog = b.create();

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String email = emails.get(position);
				ProtocolHandler.initializePoll(poll, email, con);
				dialog.dismiss();
			}
		});

		dialog.show();

	}

	private static void initializePoll(Poll poll, String email, Context con) {
		int voterIdx, tallierIdx;
		Voter voter = null;
		Tallier tallier = null;

		voterIdx = poll.getVoters().indexOf(email);
		tallierIdx = poll.getVoters().indexOf(email);

		if (voterIdx >= 0) {
			voter = new Voter(email, poll);
		}

		if (tallierIdx >= 0) {
			tallier = new Tallier(email, poll);
		}

		DiskPersister.getInst().save(poll, voter, tallier, con);
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

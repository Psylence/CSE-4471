package edu.osu.AU13.cse4471.securevote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.json.JSONArray;
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
import android.widget.Toast;
import edu.osu.AU13.cse4471.securevote.JSONUtils.JSONDeserializer;
import edu.osu.AU13.cse4471.securevote.math.GroupElement;
import edu.osu.AU13.cse4471.securevote.ui.CreatePoll;

public class ProtocolHandler {
	public static void handle(JSONObject json, Context con) {
		try {
			String phase = json.getString(Constants.JSON_PHASE);
			if (Constants.PHASE_NEWPOLL.equals(phase)) {
				ProtocolHandler.newPoll(json, con);
			} else if (Constants.PHASE_VOTE.equals(phase)) {
				ProtocolHandler.receiveVote(json, con);
			} else if (Constants.PHASE_PUBLIC_KEY.equals(phase)) {
				ProtocolHandler.receivePublicKey(json, con);
			} else if (Constants.PHASE_SHARE.equals(phase)) {
				ProtocolHandler.receiveShare(json, con);
			}

		} catch (JSONException e) {
			Log.e(ProtocolHandler.class.getSimpleName(), "JSON parse error", e);
		}
	}

	private static void receiveShare(JSONObject json, Context con)
			throws JSONException {
		UUID id = UUID.fromString(json.getString(Constants.JSON_POLL_ID));
		Tallier t = DiskPersister.getInst().loadTallier(id, con);
		if (t == null) {
			Toast.makeText(con, "You ain't a tallier", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		Poll p = t.getPoll();

		EncryptedPoint pt = new EncryptedPoint.Deserializer(p.getGroup())
				.fromJson(json.getJSONObject(Constants.JSON_SHARE));

		t.receiveResult(con, pt);
	}

	private static void receivePublicKey(JSONObject json, Context con)
			throws JSONException {
		UUID id = UUID.fromString(json.getString(Constants.JSON_POLL_ID));
		Voter v = DiskPersister.getInst().loadVoter(id, con);
		Poll p = v.getPoll();
		PublicKey key = (new PublicKey.Deserializer(p.getGroup()))
				.fromJson(json.getJSONObject(Constants.JSON_KEY));
		String email = json.getString(Constants.JSON_KEY_FROM);
		v.receiveKey(con, email, key);
	}

	private static void receiveVote(JSONObject json, Context con)
			throws JSONException {

		UUID id = UUID.fromString(json.getString(Constants.JSON_POLL_ID));
		Tallier t = DiskPersister.getInst().loadTallier(id, con);

		if (t == null) {
			Toast.makeText(
					con,
					"You don't seem to be a tallier in the poll you just opened.",
					Toast.LENGTH_SHORT).show();
			return;
		}

		Poll poll = t.getPoll();

		List<EncryptedPoint> points = new ArrayList<EncryptedPoint>();

		JSONArray arr = json.getJSONArray(Constants.JSON_ENCR_POINTS);

		JSONDeserializer<EncryptedPoint> deser = new EncryptedPoint.Deserializer(
				poll.getGroup());
		for (int i = 0, l = arr.length(); i < l; i++) {
			points.add(deser.fromJson(arr.getJSONObject(i)));
		}

		GroupElement hiddenVote = poll.getGroup().elementFromString(
				json.getString(Constants.JSON_HIDDEN_VOTE));

		t.receiveVote(con, hiddenVote, points,
				json.getString(Constants.JSON_VOTE_FROM));

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

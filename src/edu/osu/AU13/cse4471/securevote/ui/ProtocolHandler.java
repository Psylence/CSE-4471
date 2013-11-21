package edu.osu.AU13.cse4471.securevote.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import edu.osu.AU13.cse4471.securevote.Constants;
import edu.osu.AU13.cse4471.securevote.Poll;
import edu.osu.AU13.cse4471.securevote.PrivateKey;
import edu.osu.AU13.cse4471.securevote.Tallier;
import edu.osu.AU13.cse4471.securevote.User;

public class ProtocolHandler {
  public void handle(JSONObject json, Activity activity) throws JSONException {
    if (!json.has(Constants.JSON_PHASE)) {
      throw new JSONException("Bad JSON: no phase");
    }
    if (!json.has(Constants.JSON_POLL)) {
      throw new JSONException("Bad JSON: no poll");
    }

    String phase = json.getString(Constants.JSON_PHASE);
    Poll p = new Poll(json.getJSONObject(Constants.JSON_POLL));

    if (Constants.PHASE_REQPUBKEY.equals(phase)) {
      int whichTallier = json.getInt(Constants.JSON_TALLIERNUM);

      if (!ProtocolHandler.confirmIdentity(p.getTalliers().get(whichTallier))) {
        throw new JSONException("Wrong user");
      }

      PrivateKey key = new PrivateKey(p.getGroup(), p.getg());
      // p.setTallierSecretKey(key);
      p.getTalliers().get(whichTallier).setPublicKey(key.getPublicKey());

      if (whichTallier < p.getTalliers().size()) {
        sendRequestPublicKey(p, p.getTalliers().get(whichTallier + 1), activity);
      } else {
        Log.i(ProtocolHandler.class.getSimpleName(),
            "Done collecting public keys: " + p.toJson());
      }
    } else {
      throw new JSONException("Unknown phase: " + phase);
    }
  }

  public void sendRequestPublicKey(Poll p, Tallier t, Activity activity) {
    // Create the email payload
    String payload;
    try {
      JSONObject obj = new JSONObject();
      obj.put(Constants.JSON_PHASE, Constants.PHASE_REQPUBKEY);
      obj.put(Constants.JSON_TALLIERNUM, 0);
      obj.put(Constants.JSON_POLL, p.toJson());
      payload = obj.toString();
    } catch (JSONException e) {
      Log.e(CreatePoll.class.getSimpleName(), "Error serializing Poll: ", e);
      Toast.makeText(activity, "Error serializing poll for email",
          Toast.LENGTH_SHORT).show();
      return;
    }

    // Format and send the email
    // String title = activity.getString(R.string.message_pubkeyreq_title);
    // String body = String.format(Locale.US,
    // activity.getString(R.string.message_pubkeyreq_body_fmt), p.getTitle(),
    // p.getDesc());

    // t.sendEmail(title, body, payload, activity);
  }

  private static boolean confirmIdentity(User user) {
    // Authenticate the current user
    return true;
  }

  public static ProtocolHandler getInst() {
    return new ProtocolHandler();
  }

}
